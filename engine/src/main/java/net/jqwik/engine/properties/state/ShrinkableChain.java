package net.jqwik.engine.properties.state;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.*;

public class ShrinkableChain<T> implements Shrinkable<Chain<T>> {

	private final long randomSeed;
	private final Supplier<T> initialSupplier;
	private final Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>> chainGenerator;
	private final int maxSize;
	private final List<Tuple3<Long, Boolean, Shrinkable<Chains.Mutator<T>>>> iterations;

	public ShrinkableChain(long randomSeed, Supplier<T> initialSupplier, Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>> chainGenerator, int maxSize) {
		this.randomSeed = randomSeed;
		this.initialSupplier = initialSupplier;
		this.chainGenerator = chainGenerator;
		this.maxSize = maxSize;
		this.iterations = new ArrayList<>();
	}

	@Override
	public Chain<T> value() {
		return new ChainInstance();
	}

	@Override
	public Stream<Shrinkable<Chain<T>>> shrink() {
		return Stream.empty();
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
			next = generator.next(new Random(nextSeed));
			iterations.add(Tuple.of(nextSeed, stateHasBeenAccessed.get(), next));
			return next;
		}
	}
}
