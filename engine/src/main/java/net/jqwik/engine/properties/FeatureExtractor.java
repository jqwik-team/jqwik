package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@FunctionalInterface
public interface FeatureExtractor<T> extends Function<T, Object> {

	default boolean isUniqueIn(T value, List<T> elements) {
		Set<Object> elementFeatures = elements.stream().map(this).collect(Collectors.toSet());
		return !elementFeatures.contains(this.apply(value));
	}

	default boolean areUnique(List<T> elements) {
		Set<Object> elementFeatures = elements.stream().map(this).collect(Collectors.toSet());
		return elementFeatures.size() == elements.size();
	}
}
