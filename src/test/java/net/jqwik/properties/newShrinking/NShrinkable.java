package net.jqwik.properties.newShrinking;

import java.util.function.*;

public interface NShrinkable<T> extends Comparable<NShrinkable<T>> {

	T value();

	ShrinkingSequence<T> shrink(Falsifier<T> falsifier);

	ShrinkingDistance distance();

	default <U> NShrinkable<U> map(Function<T, U> mapper) {
		return new NMappedShrinkable<>(this, mapper);
	}

	@Override
	default int compareTo(NShrinkable<T> other) {
		return this.distance().compareTo(other.distance());
	}

	default boolean isSmallerThan(NShrinkable<T> other) {
		return this.distance().compareTo(other.distance()) < 0;
	}
}
