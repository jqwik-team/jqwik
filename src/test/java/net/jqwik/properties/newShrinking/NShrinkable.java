package net.jqwik.properties.newShrinking;

public interface NShrinkable<T> extends Comparable<NShrinkable<T>> {

	T value();

	ShrinkingSequence<T> shrink(Falsifier<T> falsifier);

	ShrinkingDistance distance();

	@Override
	default int compareTo(NShrinkable<T> other) {
		return this.distance().compareTo(other.distance());
	}

	default boolean isSmallerThan(NShrinkable<T> other) {
		return this.distance().compareTo(other.distance()) < 0;
	}
}
