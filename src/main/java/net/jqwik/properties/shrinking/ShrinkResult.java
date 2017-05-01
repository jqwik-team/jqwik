package net.jqwik.properties.shrinking;

import java.util.*;

public class ShrinkResult<T> implements Comparable<ShrinkResult> {

	private final ShrinkValue<T> shrinkValue;
	private final AssertionError assertionError;

	public static<T> ShrinkResult<T> of(ShrinkValue<T> shrinkValue, AssertionError assertionError) {
		return new ShrinkResult<T>(shrinkValue, assertionError);
	}

	private ShrinkResult(ShrinkValue<T> shrinkValue, AssertionError assertionError) {
		this.shrinkValue = shrinkValue;
		this.assertionError = assertionError;
	}

	public T value() {
		return shrinkValue.value();
	}

	public int distanceToTarget() {
		return shrinkValue.distanceToTarget();
	}

	public Optional<AssertionError> error() {
		return Optional.ofNullable(assertionError);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ShrinkResult<?> that = (ShrinkResult<?>) o;
		return Objects.equals(shrinkValue, that.shrinkValue) &&
			Objects.equals(assertionError, that.assertionError);
	}

	@Override
	public int hashCode() {
		return Objects.hash(shrinkValue, assertionError);
	}

	@Override
	public int compareTo(ShrinkResult other) {
		return Integer.compare(distanceToTarget(), other.distanceToTarget());
	}

	@Override
	public String toString() {
		return String.format("ShrinkResult[%s:%d:%s]", value(), distanceToTarget(), assertionError);
	}
}
