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
	private final int size;

	public ShrinkableChain(long randomSeed, Supplier<T> initialSupplier, Function<Supplier<T>, Arbitrary<T>> chainGenerator, int size) {
		this.randomSeed = randomSeed;
		this.initialSupplier = initialSupplier;
		this.chainGenerator = chainGenerator;
		this.size = size;
	}

	@Override
	public Chain<T> value() {
		return () -> new ChainIterator(initialSupplier.get());
	}

	@Override
	public Stream<Shrinkable<Chain<T>>> shrink() {
		return Stream.empty();
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.of(size);
	}

	private class ChainIterator implements Iterator<T> {

		private final Random random = new Random(randomSeed);
		private T current;
		private int steps = 0;

		private ChainIterator(T initial) {
			this.current = initial;
		}

		@Override
		public boolean hasNext() {
			if (steps < size) {
				return true;
			}
			return false;
		}

		@Override
		public T next() {
			Supplier<T> currentSupplier = () -> current;
			RandomGenerator<T> generator = chainGenerator.apply(currentSupplier).generator(size);
			Shrinkable<T> next = generator.next(random);
			current = next.value();
			steps++;
			return current;
		}
	}
}
