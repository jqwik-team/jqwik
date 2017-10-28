package net.jqwik.properties.arbitraries;

import net.jqwik.api.Arbitrary;

public abstract class ArbitraryWrapper<W, T> implements Arbitrary<T> {

	protected final Arbitrary<W> wrapped;

	public ArbitraryWrapper(Arbitrary<W> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public Arbitrary<?> inner() {
		return wrapped.inner();
	}

}
