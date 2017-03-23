package net.jqwik.execution.properties;

import javaslang.test.Arbitrary;
import javaslang.test.Gen;

public class SizedArbitrary<T> implements Arbitrary<T> {

	private final Arbitrary<T> wrapped;
	private final int effectiveSize;

	public SizedArbitrary(Arbitrary<T> wrapped, int effectiveSize) {
		this.wrapped = wrapped;
		this.effectiveSize = effectiveSize;
	}

	@Override
	public Gen<T> apply(int size) {
		return wrapped.apply(effectiveSize);
	}

}
