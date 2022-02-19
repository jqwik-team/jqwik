package net.jqwik.engine.properties.state;

import net.jqwik.api.*;
import net.jqwik.api.state.Chain;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.*;

public class ShrinkableChain<T> implements Shrinkable<Chain<T>> {

	private final long randomSeed;
	private final Supplier<T> initialSupplier;
	private final Function<Supplier<T>, Arbitrary<T>> chainGenerator;
	private final int maxSize;
	private final List<Shrinkable<T>> iterations;

	public ShrinkableChain(long randomSeed, Supplier<T> initialSupplier, Function<Supplier<T>, Arbitrary<T>> chainGenerator, int maxSize) {
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
		public List<T> iterations() {
			return iterations.stream().map(Shrinkable::value).collect(Collectors.toList());
		}

		@Override
		public int maxSize() {
			return maxSize;
		}
	}

	private class ChainIterator implements Iterator<T> {

		private final Random random = new Random(randomSeed);
		private int steps = 0;
		private T current;

		private ChainIterator(T initial) {
			this.current = initial;
		}

		@Override
		public boolean hasNext() {
			if (steps < maxSize) {
				return true;
			}
			return false;
		}

		@Override
		public T next() {
			Supplier<T> currentSupplier = () -> current;
			RandomGenerator<T> generator = chainGenerator.apply(currentSupplier).generator(maxSize);
			Shrinkable<T> next = generator.next(random);
			iterations.add(next);
			steps++;
			current = next.value();
			return current;
		}
	}
}
