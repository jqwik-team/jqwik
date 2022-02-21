package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class ShrinkableChain<T> implements Shrinkable<Chain<T>> {

	private final long randomSeed;
	private final Supplier<T> initialSupplier;
	private final List<Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>>> chainGenerators;
	private final int maxSize;
	private final List<Iteration<T>> iterations;

	public ShrinkableChain(
		long randomSeed,
		Supplier<T> initialSupplier,
		List<Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>>> chainGenerators,
		int maxSize
	) {
		this(randomSeed, initialSupplier, chainGenerators, maxSize, new ArrayList<>());
	}

	private ShrinkableChain(
		long randomSeed, Supplier<T> initialSupplier,
		List<Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>>> chainGenerators,
		int maxSize,
		List<Iteration<T>> iterations
	) {
		this.randomSeed = randomSeed;
		this.initialSupplier = initialSupplier;
		this.chainGenerators = chainGenerators;
		this.maxSize = maxSize;
		this.iterations = iterations;
	}

	@Override
	public Chain<T> value() {
		return new ChainInstance();
	}

	@Override
	public Stream<Shrinkable<Chain<T>>> shrink() {
		// TODO: Shrink all trailing iterations without access to state together
		return shrinkAllIterations();
		// return Stream.concat(
		// 	shrinkLastIteration(),
		// 	shrinkButLastIteration()
		// );
	}

	private Stream<Shrinkable<Chain<T>>> shrinkAllIterations() {
		return IntStream.range(0, iterations.size())
						.boxed()
						.flatMap(this::shrinkIteration);
	}

	private Stream<Shrinkable<Chain<T>>> shrinkIteration(int indexToShrink) {
		Iteration<T> iterationToShrink = iterations.get(indexToShrink);
		Shrinkable<Chains.Mutator<T>> toShrink = iterationToShrink.shrinkable;

		return toShrink.shrink().map(shrunk -> {
			List<Iteration<T>> shrunkIterations = iterations.stream().limit(indexToShrink)
														 .collect(Collectors.toList());
			Iteration<T> shrunkIteration = new Iteration<>(iterationToShrink.randomSeed, iterationToShrink.stateHasBeenAccessed, shrunk);
			shrunkIterations.add(shrunkIteration);
			return cloneWith(shrunkIterations);
		});
	}

	private ShrinkableChain<T> cloneWith(List<Iteration<T>> shrunkIterations) {
		return new ShrinkableChain<>(
			randomSeed,
			initialSupplier,
			chainGenerators,
			maxSize,
			shrunkIterations
		);
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.of(maxSize);
	}

	private class ChainInstance implements Chain<T> {

		@Override
		public Iterator<T> start() {
			return new ChainIterator(initialSupplier.get());
		}

		@Override
		public int countIterations() {
			return iterations.size();
		}

		@Override
		public int maxSize() {
			return maxSize;
		}
	}

	private class ChainIterator implements Iterator<T> {

		private final Random random = SourceOfRandomness.newRandom(randomSeed);
		private int steps = 0;
		private T current;

		private ChainIterator(T initial) {
			this.current = initial;
		}

		@Override
		public boolean hasNext() {
			return steps < maxSize;
		}

		@Override
		public T next() {

			// Create deterministic random in order to be able to compare on rerun
			long nextSeed = random.nextLong();

			Shrinkable<Chains.Mutator<T>> next = null;
			if (steps < iterations.size()) {
				next = rerunStep(nextSeed);
			} else {
				next = runNewStep(nextSeed);
			}
			steps++;
			current = next.value().apply(current);
			return current;
		}

		private Shrinkable<Chains.Mutator<T>> rerunStep(long nextSeed) {
			Shrinkable<Chains.Mutator<T>> next;
			Iteration<T> iteration = iterations.get(steps);
			if (nextSeed == iteration.randomSeed) {
				next = iteration.shrinkable;
			} else {
				// TODO: Maybe this should delete rest of save iterations.
				throw new RuntimeException("Generation mismatch while rerunning chain");
			}
			return next;
		}

		private Shrinkable<Chains.Mutator<T>> runNewStep(long nextSeed) {
			Shrinkable<Chains.Mutator<T>> next;
			AtomicBoolean stateHasBeenAccessed = new AtomicBoolean(false);
			Supplier<T> currentSupplier = () -> {
				stateHasBeenAccessed.set(true);
				return current;
			};
			Random random = SourceOfRandomness.newRandom(nextSeed);
			Arbitrary<Chains.Mutator<T>> arbitrary = null;
			while (arbitrary == null) {
				int generatorIndex = random.nextInt(chainGenerators.size());
				Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>> chainGenerator = chainGenerators.get(generatorIndex);
				arbitrary = chainGenerator.apply(currentSupplier);
			}
			RandomGenerator<Chains.Mutator<T>> generator = arbitrary.generator(maxSize);
			next = generator.next(random);
			iterations.add(new Iteration<>(nextSeed, stateHasBeenAccessed.get(), next));
			return next;
		}
	}

	private static class Iteration<T> {
		final long randomSeed;
		final boolean stateHasBeenAccessed;
		final Shrinkable<Chains.Mutator<T>> shrinkable;

		private Iteration(
			long randomSeed,
			boolean stateHasBeenAccessed,
			Shrinkable<Chains.Mutator<T>> shrinkable
		) {
			this.randomSeed = randomSeed;
			this.stateHasBeenAccessed = stateHasBeenAccessed;
			this.shrinkable = shrinkable;
		}
	}
}
