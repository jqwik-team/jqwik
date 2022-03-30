package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.*;

public class ShrinkableChain<T> implements Shrinkable<Chain<T>> {

	private final long randomSeed;
	private final Supplier<? extends T> initialSupplier;
	private final Function<Random, TransformerProvider<T>> providerGenerator;
	private final int maxTransformations;
	private final int genSize;
	private final List<ShrinkableChainIteration<T>> iterations;
	private final Supplier<ChangeDetector<T>> changeDetectorSupplier;

	public ShrinkableChain(
		long randomSeed,
		Supplier<? extends T> initialSupplier,
		Function<Random, TransformerProvider<T>> providerGenerator,
		Supplier<ChangeDetector<T>> changeDetectorSupplier,
		int maxTransformations,
		int genSize
	) {
		this(randomSeed, initialSupplier, providerGenerator, changeDetectorSupplier, maxTransformations, genSize, new ArrayList<>());
	}

	private ShrinkableChain(
		long randomSeed, Supplier<? extends T> initialSupplier,
		Function<Random, TransformerProvider<T>> providerGenerator,
		Supplier<ChangeDetector<T>> changeDetectorSupplier,
		int maxTransformations,
		int genSize,
		List<ShrinkableChainIteration<T>> iterations
	) {
		this.randomSeed = randomSeed;
		this.initialSupplier = initialSupplier;
		this.providerGenerator = providerGenerator;
		this.changeDetectorSupplier = changeDetectorSupplier;
		this.maxTransformations = maxTransformations;
		this.genSize = genSize;
		this.iterations = iterations;
	}

	@Override
	@NotNull
	public Chain<T> value() {
		return new ChainInstance();
	}

	@Override
	@NotNull
	public Stream<Shrinkable<Chain<T>>> shrink() {
		return new ShrinkableChainShrinker<>(this, iterations, maxTransformations).shrink();
	}

	ShrinkableChain<T> cloneWith(List<ShrinkableChainIteration<T>> shrunkIterations, int newMaxSize) {
		return new ShrinkableChain<>(
			randomSeed,
			initialSupplier,
			providerGenerator,
			changeDetectorSupplier,
			newMaxSize,
			genSize,
			shrunkIterations
		);
	}

	@Override
	@NotNull
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
		private boolean endOfChain = false;

		private ChainIterator(T initial) {
			this.current = initial;
		}

		@Override
		public boolean hasNext() {
			if (!initialSupplied) {
				return true;
			}
			if (endOfChain) {
				return false;
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
			Transformer<T> transformer = null;

			synchronized (ShrinkableChain.this) {
				Shrinkable<Transformer<T>> next = null;
				if (steps < iterations.size()) {
					next = rerunStep(nextSeed);
				} else {
					next = runNewStep(nextSeed);
				}
				transformer = next.value();
				if (transformer.equals(Transformer.END_OF_CHAIN)) {
					endOfChain = true;
				}

				current = transformState(transformer, current);
				return current;
			}
		}

		private T transformState(Transformer<T> transformer, T before) {
			ChangeDetector<T> changeDetector = changeDetectorSupplier.get();
			changeDetector.before(before);
			try {
				T after = transformer.apply(before);
				boolean stateHasChanged = changeDetector.hasChanged(after);
				ShrinkableChainIteration<T> currentIteration = iterations.get(steps);
				iterations.set(steps, currentIteration.withStateChange(stateHasChanged));
				return after;
			} finally {
				steps++;
			}
		}

		private Shrinkable<Transformer<T>> rerunStep(long nextSeed) {
			ShrinkableChainIteration<T> iteration = iterations.get(steps);
			return iteration.shrinkable;
		}

		private Shrinkable<Transformer<T>> runNewStep(long nextSeed) {
			Random random = SourceOfRandomness.newRandom(nextSeed);
			Tuple2<Arbitrary<Transformer<T>>, Boolean> arbitraryAccessTuple = nextTransformerArbitrary(random);
			Arbitrary<Transformer<T>> arbitrary = arbitraryAccessTuple.get1();
			boolean stateHasBeenAccessed = arbitraryAccessTuple.get2();

			RandomGenerator<Transformer<T>> generator = arbitrary.generator(genSize);
			Shrinkable<Transformer<T>> next = generator.next(random);
			iterations.add(new ShrinkableChainIteration<>(nextSeed, stateHasBeenAccessed, next));
			return next;
		}

		private Tuple2<Arbitrary<Transformer<T>>, Boolean> nextTransformerArbitrary(Random random) {
			return MaxTriesLoop.loop(
				() -> true,
				arbitraryAccessTuple -> {
					Function<Supplier<T>, Arbitrary<Transformer<T>>> chainGenerator = providerGenerator.apply(random);
					AtomicBoolean stateHasBeenAccessed = new AtomicBoolean(false);
					Supplier<T> supplier = () -> {
						stateHasBeenAccessed.set(true);
						return current;
					};

					Arbitrary<Transformer<T>> arbitrary = chainGenerator.apply(supplier);
					if (arbitrary == null) {
						return Tuple.of(false, null);
					}
					return Tuple.of(true, Tuple.of(arbitrary, stateHasBeenAccessed.get()));
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
