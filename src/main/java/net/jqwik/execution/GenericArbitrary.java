package net.jqwik.execution;

import net.jqwik.api.Arbitrary;
import net.jqwik.properties.RandomGenerator;
import net.jqwik.properties.arbitraries.ArbitraryWrapper;

class GenericArbitrary extends ArbitraryWrapper<Object, Object> {

	GenericArbitrary(Arbitrary wrapped) {
		super(wrapped);
	}

	@Override
	public RandomGenerator<Object> generator(int tries) {
		return wrapped.generator(tries);
	}
}
