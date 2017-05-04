package net.jqwik.properties;

import net.jqwik.properties.shrinking.*;

public abstract class ArbitraryWrapper<T> implements Arbitrary<T> {

	protected final Arbitrary<T> wrapped;

	public ArbitraryWrapper(Arbitrary<T> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public RandomGenerator<T> generator(int tries) {
		return wrapped.generator(tries);
	}

	@Override
	public Arbitrary<?> inner() {
		return wrapped.inner();
	}

	@Override
	public Shrinkable<T> shrinkableFor(T value) {
		return wrapped.shrinkableFor(value);
	}
}
