package net.jqwik.api;

import net.jqwik.engine.properties.shrinking.*;

import java.util.function.*;

public interface Shrinkable<T> extends Comparable<Shrinkable<T>> {

	static <T> Shrinkable<T> unshrinkable(T value) {
		return new Unshrinkable<>(value);
	}

	T value();

	ShrinkingSequence<T> shrink(Falsifier<T> falsifier);

	ShrinkingDistance distance();

	default <U> Shrinkable<U> map(Function<T, U> mapper) {
		return new MappedShrinkable<>(this, mapper);
	}

	default Shrinkable<T> filter(Predicate<T> filter) {
		return new FilteredShrinkable<>(this, filter);
	}

	@Override
	default int compareTo(Shrinkable<T> other) {
		return this.distance().compareTo(other.distance());
	}

	default boolean isSmallerThan(Shrinkable<T> other) {
		return this.distance().compareTo(other.distance()) < 0;
	}

	default <U> Shrinkable<U> flatMap(Function<T, Arbitrary<U>> flatMapper, int tries, long randomSeed) {
		return new FlatMappedShrinkable<>(this, flatMapper, tries, randomSeed);
	}
}
