package net.jqwik.newArbitraries;

import java.util.*;

public class NShrinkResult<T> {

	private final T shrinkValue;
	private final Throwable throwable;

	public static<T> NShrinkResult<T> of(T shrinkValue, Throwable assertionError) {
		return new NShrinkResult<T>(shrinkValue, assertionError);
	}

	private NShrinkResult(T shrinkValue, Throwable throwable) {
		this.shrinkValue = shrinkValue;
		this.throwable = throwable;
	}

	public T value() {
		return shrinkValue;
	}

	public Optional<Throwable> throwable() {
		return Optional.ofNullable(throwable);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NShrinkResult<?> that = (NShrinkResult<?>) o;
		return Objects.equals(shrinkValue, that.shrinkValue) &&
			Objects.equals(throwable, that.throwable);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shrinkValue, throwable);
	}

	@Override
	public String toString() {
		return String.format("NShrinkResult[%s:%s]", value(), throwable);
	}
}
