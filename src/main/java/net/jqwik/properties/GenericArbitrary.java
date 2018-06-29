package net.jqwik.properties;

import net.jqwik.api.*;

class GenericArbitrary implements Arbitrary<Object> {

	private final Arbitrary wrapped;

	GenericArbitrary(Arbitrary<?> wrapped) {
		this.wrapped = wrapped;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RandomGenerator<Object> generator(int genSize) {
		return wrapped.generator(genSize);
	}
}
