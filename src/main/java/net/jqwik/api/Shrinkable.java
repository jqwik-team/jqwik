package net.jqwik.api;

import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.function.*;

public interface Shrinkable<T> {

	static <T> Shrinkable<T> unshrinkable(T value) {
		return new Unshrinkable<>(value);
	}

	Set<ShrinkResult<Shrinkable<T>>> shrinkNext(Predicate<T> falsifier);

	T value();

	int distance();

	default <U> Shrinkable<U> map(Function<T, U> mapper) {
		return new MappedShrinkable<>(this, mapper);
	}

}

