package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

@FunctionalInterface
public interface FeatureExtractor<T> extends Function<T, Object> {

	static <T> boolean checkUniquenessIn(Collection<FeatureExtractor<T>> extractors, Shrinkable<T> shrinkable, List<Shrinkable<T>> shrinkables) {
		if (extractors.isEmpty()) {
			return true;
		}
		T value = shrinkable.value();
		List<T> elements = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return checkUniquenessIn(extractors, value, elements);
	}

	static <T> boolean checkUniquenessIn(Collection<FeatureExtractor<T>> extractors, T value, List<T> elements) {
		for (FeatureExtractor<T> extractor : extractors) {
			if (extractor.isNotUniqueIn(value, elements)) {
				return false;
			}
		}
		return true;
	}

	static <T> boolean checkUniqueness(Collection<FeatureExtractor<T>> extractors, List<Shrinkable<T>> shrinkables) {
		if (extractors.isEmpty()) {
			return true;
		}
		List<T> elements = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		for (FeatureExtractor<T> extractor : extractors) {
			if (extractor.areNotUnique(elements)) {
				return false;
			}
		}
		return true;
	}

	default boolean isNotUniqueIn(T value, List<T> elements) {
		Set<Object> elementFeatures = elements.stream().map(this).collect(Collectors.toSet());
		return elementFeatures.contains(this.apply(value));
	}

	default boolean areNotUnique(List<T> elements) {
		Set<Object> elementFeatures = elements.stream().map(this).collect(Collectors.toSet());
		return elementFeatures.size() != elements.size();
	}
}
