package net.jqwik.execution;


import net.jqwik.properties.*;

class GenericArbitrary extends ArbitraryWrapper<Object> {

	GenericArbitrary(Arbitrary wrapped) {
		super(wrapped);
	}

	@Override
	public RandomGenerator<Object> generator(int tries) {
		return wrapped.generator(tries);
	}
}
