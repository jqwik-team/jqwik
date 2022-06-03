package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;

@FunctionalInterface
public interface FeatureExtractor<T> extends Function<T, Object> {

	static <T> FeatureExtractor<T> identity() {
		return t -> t;
	}

	default boolean isUniqueIn(T value, Collection<T> elements) {
		if (this == identity()) {
			return !elements.contains(value);
		}
		Object feature = apply(value);
		return elements.stream()
					   .map(this)
					   .noneMatch(x -> Objects.equals(x, feature));
	}

	default boolean areUnique(Collection<T> elements) {
		long uniqueCount = elements.stream().map(this).distinct().count();
		return uniqueCount == elements.size();
	}
}
