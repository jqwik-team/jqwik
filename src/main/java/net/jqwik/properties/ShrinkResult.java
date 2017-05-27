package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

public class ShrinkResult<T> {

	private final T shrunkValue;
	private final Throwable throwable;

	public static<T> ShrinkResult<T> of(T shrinkValue, Throwable assertionError) {
		return new ShrinkResult<T>(shrinkValue, assertionError);
	}

	private ShrinkResult(T shrunkValue, Throwable throwable) {
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
		ShrinkResult<?> that = (ShrinkResult<?>) o;
		return Objects.equals(shrunkValue, that.shrunkValue);
	}

	public <U> ShrinkResult<U> map(Function<T, U> mapper) {
		return new ShrinkResult<>(mapper.apply(shrunkValue), throwable);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shrunkValue);
	}

	@Override
	public String toString() {
		return String.format("ShrinkResult[%s:%s]", shrunkValue(), throwable);
	}
}
