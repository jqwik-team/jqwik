package net.jqwik.execution;

import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

class GenericArbitrary extends NArbitraryWrapper<Object, Object> {

	GenericArbitrary(NArbitrary wrapped) {
		super(wrapped);
	}

	@Override
	public NShrinkableGenerator<Object> generator(int tries) {
		return wrapped.generator(tries);
	}
}
