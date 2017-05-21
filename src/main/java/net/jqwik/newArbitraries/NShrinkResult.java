package net.jqwik.newArbitraries;

import java.util.*;

public class NShrinkResult<T> {

	private final T shrinkValue;
	private final AssertionError assertionError;

	public static<T> NShrinkResult<T> of(T shrinkValue, AssertionError assertionError) {
		return new NShrinkResult<T>(shrinkValue, assertionError);
	}

	private NShrinkResult(T shrinkValue, AssertionError assertionError) {
		this.shrinkValue = shrinkValue;
		this.assertionError = assertionError;
	}

	public T value() {
		return shrinkValue;
	}

	public Optional<AssertionError> error() {
		return Optional.ofNullable(assertionError);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NShrinkResult<?> that = (NShrinkResult<?>) o;
		return Objects.equals(shrinkValue, that.shrinkValue) &&
			Objects.equals(assertionError, that.assertionError);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shrinkValue, assertionError);
	}

	@Override
	public String toString() {
		return String.format("NShrinkResult[%s:%s]", value(), assertionError);
	}
}
