package net.jqwik.properties.newShrinking;

import java.util.*;

public interface NShrinkable<T> extends Comparable<NShrinkable<T>> {

	T value();

	Set<NShrinkable<T>> shrink();

	ShrinkingDistance distance();

	@Override
	default int compareTo(NShrinkable<T> other) {
		return this.distance().compareTo(other.distance());
	}
}
