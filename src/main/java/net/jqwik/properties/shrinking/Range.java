package net.jqwik.properties.shrinking;

public class Range<T extends Comparable> {

	public static <T extends Comparable> Range<T> of(T left, T right) {
		if (left.compareTo(right) > 0)
			return new Range<>(right, left);
		else
			return new Range<>(left, right);
	}

	private final T min;
	private final T max;

	private Range(T min, T max) {
		this.min = min;
		this.max = max;
	}

	public boolean includes(T value) {
		return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
	}

	@Override
	public String toString() {
		return String.format("%s..%s", min, max);
	}
}
