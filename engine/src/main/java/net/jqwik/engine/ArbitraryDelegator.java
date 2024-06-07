package net.jqwik.engine;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import org.jspecify.annotations.*;

public class ArbitraryDelegator<T extends @Nullable Object> extends ArbitraryDecorator<T> {

	private final Arbitrary<T> delegate;

	public ArbitraryDelegator(Arbitrary<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Arbitrary<T> arbitrary() {
		return delegate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ArbitraryDelegator<?> that = (ArbitraryDelegator<?>) o;
		return delegate.equals(that.delegate);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}
}
