package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@FunctionalInterface
public interface FeatureExtractor<T> extends Function<T, Object> {

	static <T> FeatureExtractor<T> identity() {
		return t -> t;
	}

	default Object applySafe(T t) {
		try {
			return apply(t);
		} catch (NullPointerException npe) {
			return null;
		}
	}

	default boolean isUniqueIn(T value, List<T> elements) {
		Set<Object> elementFeatures = elements.stream().map(this::applySafe).collect(Collectors.toSet());
		return !elementFeatures.contains(this.applySafe(value));
	}

	default boolean areUnique(List<T> elements) {
		Set<Object> elementFeatures = elements.stream().map(this::applySafe).collect(Collectors.toSet());
		return elementFeatures.size() == elements.size();
	}
}
