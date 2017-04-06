package net.jqwik.execution.properties;

import javaslang.test.*;

class GenericArbitrary implements Arbitrary<Object> {

	private final Arbitrary<?> wrapped;
	private final int sizePerArbitrary;

	GenericArbitrary(Arbitrary<?> wrapped, int sizePerArbitrary) {
		this.wrapped = wrapped;
		this.sizePerArbitrary = sizePerArbitrary;
	}

	@Override
	public Gen<Object> apply(int size) {
		int effectiveSize = sizePerArbitrary == 0 ? size : sizePerArbitrary;
		return (Gen<Object>) wrapped.apply(effectiveSize);
	}

	public int size() {
		return sizePerArbitrary;
	}

}
