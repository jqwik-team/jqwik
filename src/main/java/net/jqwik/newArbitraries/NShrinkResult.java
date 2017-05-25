package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NShrinkResult<T> {

	private final T shrunkValue;
	private final Throwable throwable;

	public static<T> NShrinkResult<T> of(T shrinkValue, Throwable assertionError) {
		return new NShrinkResult<T>(shrinkValue, assertionError);
	}

	private NShrinkResult(T shrunkValue, Throwable throwable) {
		this.shrunkValue = shrunkValue;
		this.throwable = throwable;
	}

	public T shrunkValue() {
		return shrunkValue;
	}

	public Optional<Throwable> throwable() {
		return Optional.ofNullable(throwable);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NShrinkResult<?> that = (NShrinkResult<?>) o;
		return Objects.equals(shrunkValue, that.shrunkValue) &&
			Objects.equals(throwable, that.throwable);
	}

	public <U> NShrinkResult<U> map(Function<T, U> mapper) {
		return new NShrinkResult<>(mapper.apply(shrunkValue), throwable);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shrunkValue, throwable);
	}

	@Override
	public String toString() {
		return String.format("NShrinkResult[%s:%s]", shrunkValue(), throwable);
	}
}
