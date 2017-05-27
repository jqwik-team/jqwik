package net.jqwik.properties.arbitraries;

import net.jqwik.properties.*;

public abstract class NArbitraryWrapper<W, T> implements Arbitrary<T> {

	protected final Arbitrary<W> wrapped;

	public NArbitraryWrapper(Arbitrary<W> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public Arbitrary<?> inner() {
		return wrapped.inner();
	}

}
