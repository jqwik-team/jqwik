package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.*;

public class ShrinkableChain<T> implements Shrinkable<Chain<T>> {

	private final long randomSeed;
	private final Supplier<? extends T> initialSupplier;
	private final Function<Random, TransformerProvider<T>> providerGenerator;
	private final int maxTransformations;
	private final List<ShrinkableChainIteration<T>> iterations;

	public ShrinkableChain(
		long randomSeed,
		Supplier<? extends T> initialSupplier,
		Function<Random, TransformerProvider<T>> providerGenerator,
		int maxTransformations
	) {
		this(randomSeed, initialSupplier, providerGenerator, maxTransformations, new ArrayList<>());
	}

	private ShrinkableChain(
		long randomSeed, Supplier<? extends T> initialSupplier,
		Function<Random, TransformerProvider<T>> providerGenerator,
		int maxTransformations,
		List<ShrinkableChainIteration<T>> iterations
	) {
		this.randomSeed = randomSeed;
		this.initialSupplier = initialSupplier;
		this.providerGenerator = providerGenerator;
		this.maxTransformations = maxTransformations;
		this.iterations = iterations;
	}

	@Override
	public Chain<T> value() {
		return new ChainInstance();
	}

	@Override
	public Stream<Shrinkable<Chain<T>>> shrink() {
		return new ShrinkableChainShrinker<>(this, iterations, maxTransformations).shrink();
	}

	ShrinkableChain<T> cloneWith(List<ShrinkableChainIteration<T>> shrunkIterations, int newMaxSize) {
		return new ShrinkableChain<>(
			randomSeed,
			initialSupplier,
			providerGenerator,
			newMaxSize,
			shrunkIterations
		);
	}

	@Override
	public ShrinkingDistance distance() {
		List<Shrinkable<Transformer<T>>> shrinkablesForDistance = new ArrayList<>();
		for (int i = 0; i < maxTransformations; i++) {
			if (i < iterations.size()) {
				shrinkablesForDistance.add(iterations.get(i).shrinkable);
			} else {
				shrinkablesForDistance.add(Shrinkable.unshrinkable(t -> t));
			}
		}
		return ShrinkingDistance.forCollection(shrinkablesForDistance);
	}

	@Override
	public String toString() {
		return String.format("ShrinkableChain[maxSize=%s, iterations=%s]", maxTransformations, iterations);
	}

	private class ChainInstance implements Chain<T> {

		@Override
		@NotNull
		public Iterator<T> start() {
			return new ChainIterator(initialSupplier.get());
		}

		@Override
		public int maxTransformations() {
			return maxTransformations;
		}

		@Override
		@NotNull
		public List<String> transformations() {
			return iterations.stream().map(i -> i.shrinkable.value().transformation()).collect(Collectors.toList());
		}
	}

	private class ChainIterator implements Iterator<T> {

		private final Random random = SourceOfRandomness.newRandom(randomSeed);
		private int steps = 0;
		private T current;
		private boolean initialSupplied = false;

		private ChainIterator(T initial) {
			this.current = initial;
		}

		@Override
		public boolean hasNext() {
			if (!initialSupplied) {
				return true;
			}
			return steps < maxTransformations;
		}

		@Override
		public T next() {
			if (!initialSupplied) {
				initialSupplied = true;
				return current;
			}

			// Create deterministic random in order to reuse in shrinking
			long nextSeed = random.nextLong();
			Shrinkable<Transformer<T>> next = null;

			synchronized (ShrinkableChain.this) {
				if (steps < iterations.size()) {
					next = rerunStep(nextSeed);
				} else {
					next = runNewStep(nextSeed);
				}
				steps++;
			}

			current = next.value().apply(current);
			return current;
		}

		private Shrinkable<Transformer<T>> rerunStep(long nextSeed) {
			ShrinkableChainIteration<T> iteration = iterations.get(steps);
			return iteration.shrinkable;
		}

		private Shrinkable<Transformer<T>> runNewStep(long nextSeed) {
			AtomicBoolean stateHasBeenAccessed = new AtomicBoolean(false);
			Supplier<T> currentSupplier = () -> {
				stateHasBeenAccessed.set(true);
				return current;
			};
			Random random = SourceOfRandomness.newRandom(nextSeed);
			Arbitrary<Transformer<T>> arbitrary = nextTransformerArbitrary(currentSupplier, random);
			RandomGenerator<Transformer<T>> generator = arbitrary.generator(maxTransformations);
			Shrinkable<Transformer<T>> next = generator.next(random);
			iterations.add(new ShrinkableChainIteration<>(nextSeed, stateHasBeenAccessed.get(), next));
			return next;
		}

		private Arbitrary<Transformer<T>> nextTransformerArbitrary(Supplier<T> currentStateSupplier, Random random) {
			return MaxTriesLoop.loop(
				() -> true,
				arbitrary -> {
					Function<Supplier<T>, Arbitrary<Transformer<T>>> chainGenerator = providerGenerator.apply(random);
					arbitrary = chainGenerator.apply(currentStateSupplier);
					if (arbitrary == null) {
						return Tuple.of(false, arbitrary);
					}
					return Tuple.of(true, arbitrary);
				},
				maxMisses -> {
					String message = String.format("Could not generate a transformer after %s tries.", maxMisses);
					return new JqwikException(message);
				},
				1000
			);
		}
	}

}
