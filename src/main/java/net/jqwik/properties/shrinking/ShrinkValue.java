package net.jqwik.properties.shrinking;

import java.util.*;

public class ShrinkValue<T> implements ShrinkNode<T> {
	public static <T> ShrinkValue<T> of(T value, int diffFromTarget) {
		return new ShrinkValue<>(value, diffFromTarget);
	}

	private final T value;
	private final int distancePercentage;

	private ShrinkValue(T value, int distancePercentage) {
		this.value = value;
		this.distancePercentage = distancePercentage;
	}

	public T value() {
		return value;
	}

	public int distancePercentage() {
		return distancePercentage;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ShrinkValue<?> that = (ShrinkValue<?>) o;
		return distancePercentage == that.distancePercentage &&
			Objects.equals(value, that.value);
	}

	@Override
	public String toString() {
		return String.format("ShrinkValue[%s:%d]", value, distancePercentage);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, distancePercentage);
	}

	@Override
	public Iterator<ShrinkValue<T>> iterator() {
		return new Iterator<ShrinkValue<T>>() {

			private boolean done = false;

			@Override
			public boolean hasNext() {
				return !done;
			}

			@Override
			public ShrinkValue<T> next() {
				if (done)
					throw new NoSuchElementException();
				done = true;
				return ShrinkValue.this;
			}
		};
	}
}
