package net.jqwik.properties.arbitraries;

import java.util.function.*;

@SuppressWarnings("unchecked")
public class Range<T extends Comparable> {

	public static <T extends Comparable> Range<T> of(T left, T right) {
		if (left.compareTo(right) > 0)
			return new Range<>(right, left);
		else
			return new Range<>(left, right);
	}

	public final T min;
	public final T max;

	private Range(T min, T max) {
		this.min = min;
		this.max = max;
	}

	public boolean includes(T value) {
		return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
	}

	public void ifIncluded(T value, Consumer<T> consumer) {
		if (includes(value)) {
			consumer.accept(value);
		}
	}

	@Override
	public String toString() {
		return String.format("%s..%s", min, max);
	}
}
