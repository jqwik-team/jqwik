package net.jqwik.execution.properties;

import javaslang.test.Arbitrary;
import javaslang.test.Gen;

class GenericArbitrary implements Arbitrary<Object> {

	private final Arbitrary<?> wrapped;

	GenericArbitrary(Arbitrary<?> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public Gen<Object> apply(int size) {
		return (Gen<Object>) wrapped.apply(size);
	}

}
