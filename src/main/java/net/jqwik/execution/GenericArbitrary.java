package net.jqwik.execution;

import net.jqwik.newArbitraries.*;

class GenericArbitrary extends NArbitraryWrapper<Object, Object> {

	GenericArbitrary(NArbitrary wrapped) {
		super(wrapped);
	}

	@Override
	public NShrinkableGenerator<Object> generator(int tries) {
		return wrapped.generator(tries);
	}
}
