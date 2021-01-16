package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class UniquenessChecker {

	public static <T> boolean checkShrinkableUniqueIn(Collection<FeatureExtractor<T>> extractors, Shrinkable<T> shrinkable, List<Shrinkable<T>> shrinkables) {
		if (extractors.isEmpty()) {
			return true;
		}
		T value = shrinkable.value();
		List<T> elements = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return checkValueUniqueIn(extractors, value, elements);
	}

	public static <T> boolean checkValueUniqueIn(Collection<FeatureExtractor<T>> extractors, T value, Collection<T> elements) {
		for (FeatureExtractor<T> extractor : extractors) {
			if (!extractor.isUniqueIn(value, elements)) {
				return false;
			}
		}
		return true;
	}

	public static <T> boolean checkUniquenessOfShrinkables(Collection<FeatureExtractor<T>> extractors, List<Shrinkable<T>> shrinkables) {
		if (extractors.isEmpty()) {
			return true;
		}
		List<T> elements = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return checkUniquenessOfValues(extractors, elements);
	}

	public static <T> boolean checkUniquenessOfValues(Collection<FeatureExtractor<T>> extractors, Collection<T> elements) {
		for (FeatureExtractor<T> extractor : extractors) {
			if (!extractor.areUnique(elements)) {
				return false;
			}
		}
		return true;
	}

}
