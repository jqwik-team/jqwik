package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

public class ShrinkValue<T> implements Falsifiable<T> {
	public static <T> ShrinkValue<T> of(T value, int distanceToTarget) {
		return new ShrinkValue<>(value, distanceToTarget);
	}

	private final T value;
	private final int distanceToTarget;

	private ShrinkValue(T value, int distanceToTarget) {
		this.value = value;
		this.distanceToTarget = distanceToTarget;
	}

	public T value() {
		return value;
	}

	public int distanceToTarget() {
		return distanceToTarget;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ShrinkValue<?> that = (ShrinkValue<?>) o;
		return distanceToTarget == that.distanceToTarget &&
			Objects.equals(value, that.value);
	}

	@Override
	public String toString() {
		return String.format("ShrinkValue[%s:%d]", value, distanceToTarget);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, distanceToTarget);
	}

	@Override
	public Optional<ShrinkResult<T>> falsify(Predicate<T> falsifier) {
		try {
			if (falsifier.test(value()))
				return Optional.empty();
			return Optional.of(ShrinkResult.of(this, null));
		} catch (AssertionError assertionError) {
			return Optional.of(ShrinkResult.of(this, assertionError));
		} catch (Throwable any) {
			return Optional.empty();
		}
	}
}
