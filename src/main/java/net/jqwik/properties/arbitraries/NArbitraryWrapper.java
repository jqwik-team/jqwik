package net.jqwik.properties.arbitraries;

import net.jqwik.properties.*;

public abstract class NArbitraryWrapper<W, T> implements NArbitrary<T> {

	protected final NArbitrary<W> wrapped;

	public NArbitraryWrapper(NArbitrary<W> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public NArbitrary<?> inner() {
		return wrapped.inner();
	}

}
