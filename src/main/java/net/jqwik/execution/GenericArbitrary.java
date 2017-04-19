package net.jqwik.execution;


import net.jqwik.properties.*;

class GenericArbitrary implements Arbitrary<Object> {

	private final Arbitrary<?> wrapped;

	GenericArbitrary(Arbitrary<?> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public RandomGenerator<Object> generator(int tries) {
		return (RandomGenerator<Object>) wrapped.generator(tries);
	}
}
