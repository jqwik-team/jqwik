package net.jqwik.engine.properties;

import java.util.function.*;

import net.jqwik.api.*;

public class Range<T extends Comparable<T>> {

	public static <T extends Comparable<T>> Range<T> of(T min, T max) {
		if (min.compareTo(max) > 0)
			throw new JqwikException(String.format("Min value [%s] must not be greater that max value [%s].", min, max));
		return new Range<>(min, max);
	}

	public final T min;
	public final T max;

	private Range(T min, T max) {
		this.min = min;
		this.max = max;
	}

	public boolean isSingular() {
		return min.compareTo(max) == 0;
	}

	public boolean includes(T value) {
		return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
	}

	public void ifIncluded(T value, Consumer<T> consumer) {
		if (includes(value)) {
			consumer.accept(value);
		}
	}

	public <U extends Comparable<U>> Range<U> map(Function<T, U> mapper) {
		return Range.of(mapper.apply(min), mapper.apply(max));
	}

	public Range<T> withMin(T newMin, boolean minIncluded) {
		return new Range<>(newMin, max);
	}

	public Range<T> withMax(T newMax, boolean maxIncluded) {
		return new Range<>(min, newMax);
	}

	@Override
	public String toString() {
		return String.format("[%s..%s]", min, max);
	}
}
