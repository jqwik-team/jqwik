package net.jqwik.engine;

import net.jqwik.api.*;

public class ArbitraryDelegator<T> extends ArbitraryDecorator<T> {

	private final Arbitrary<T> delegate;

	public ArbitraryDelegator(Arbitrary<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Arbitrary<T> arbitrary() {
		return delegate;
	}
}
