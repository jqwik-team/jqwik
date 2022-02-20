package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.*;

public class ShrinkableChain<T> implements Shrinkable<Chain<T>> {

	private final long randomSeed;
	private final Supplier<T> initialSupplier;
	private final Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>> chainGenerator;
	private final int maxSize;
	private final List<Tuple3<Long, Boolean, Shrinkable<Chains.Mutator<T>>>> iterations;

	public ShrinkableChain(
		long randomSeed,
		Supplier<T> initialSupplier,
		Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>> chainGenerator,
		int maxSize
	) {
		this(randomSeed, initialSupplier, chainGenerator, maxSize, new ArrayList<>());
	}

	private ShrinkableChain(
		long randomSeed, Supplier<T> initialSupplier,
		Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>> chainGenerator,
		int maxSize,
		List<Tuple3<Long, Boolean, Shrinkable<Chains.Mutator<T>>>> iterations
	) {
		this.randomSeed = randomSeed;
		this.initialSupplier = initialSupplier;
		this.chainGenerator = chainGenerator;
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
						.mapToObj(i -> i)
						.flatMap(this::shrinkIteration);
	}

	private Stream<Shrinkable<Chain<T>>> shrinkIteration(int indexToShrink) {
		Tuple3<Long, Boolean, Shrinkable<Chains.Mutator<T>>> iterationToShrink = iterations.get(indexToShrink);
		Shrinkable<Chains.Mutator<T>> toShrink = iterationToShrink.get3();

		return toShrink.shrink().map(shrunk -> {
			List<Tuple3<Long, Boolean, Shrinkable<Chains.Mutator<T>>>> shrunkIterations = iterations.stream().limit(indexToShrink)
																									.collect(Collectors.toList());
			Tuple3<Long, Boolean, Shrinkable<Chains.Mutator<T>>> shrunkIteration = Tuple.of(iterationToShrink.get1(), iterationToShrink.get2(), shrunk);
			shrunkIterations.add(shrunkIteration);
			return cloneWith(shrunkIterations);
		});
	}

	private ShrinkableChain<T> cloneWith(List<Tuple3<Long, Boolean, Shrinkable<Chains.Mutator<T>>>> shrunkIterations) {
		return new ShrinkableChain<>(
			randomSeed,
			initialSupplier,
			chainGenerator,
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
			Tuple3<Long, Boolean, Shrinkable<Chains.Mutator<T>>> iteration = iterations.get(steps);
			if (nextSeed == iteration.get1()) {
				next = iteration.get3();
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
			RandomGenerator<Chains.Mutator<T>> generator = chainGenerator.apply(currentSupplier).generator(maxSize);
			next = generator.next(SourceOfRandomness.newRandom(nextSeed));
			iterations.add(Tuple.of(nextSeed, stateHasBeenAccessed.get(), next));
			return next;
		}
	}
}
