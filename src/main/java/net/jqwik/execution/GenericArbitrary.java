package net.jqwik.execution;

import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

class GenericArbitrary extends NArbitraryWrapper<Object, Object> {

	GenericArbitrary(Arbitrary wrapped) {
		super(wrapped);
	}

	@Override
	public RandomGenerator<Object> generator(int tries) {
		return wrapped.generator(tries);
	}
}
