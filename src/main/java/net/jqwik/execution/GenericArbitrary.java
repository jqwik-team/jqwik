package net.jqwik.execution;

import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

class GenericArbitrary extends ArbitraryWrapper<Object, Object> {

	GenericArbitrary(Arbitrary wrapped) {
		super(wrapped);
	}

	@Override
	public RandomGenerator<Object> generator(int tries) {
		return wrapped.generator(tries);
	}
}
