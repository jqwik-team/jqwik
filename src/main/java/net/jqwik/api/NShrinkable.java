package net.jqwik.api;

import net.jqwik.properties.shrinking.*;

import java.util.function.*;

public interface NShrinkable<T> extends Comparable<NShrinkable<T>> {

	static <T> NShrinkable<T> unshrinkable(T value) {
		return new NUnshrinkable<>(value);
	}

	T value();

	ShrinkingSequence<T> shrink(Falsifier<T> falsifier);

	ShrinkingDistance distance();

	default <U> NShrinkable<U> map(Function<T, U> mapper) {
		return new NMappedShrinkable<>(this, mapper);
	}

	default NShrinkable<T> filter(Predicate<T> filter) {
		return new NFilteredShrinkable<>(this, filter);
	}

	@Override
	default int compareTo(NShrinkable<T> other) {
		return this.distance().compareTo(other.distance());
	}

	default boolean isSmallerThan(NShrinkable<T> other) {
		return this.distance().compareTo(other.distance()) < 0;
	}

}
