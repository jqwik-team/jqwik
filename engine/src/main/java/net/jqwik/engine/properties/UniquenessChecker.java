package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

public class UniquenessChecker {

	public static <T extends @Nullable Object> boolean checkShrinkableUniqueIn(Collection<? extends FeatureExtractor<T>> extractors, Shrinkable<T> shrinkable, List<? extends Shrinkable<T>> shrinkables) {
		if (extractors.isEmpty()) {
			return true;
		}
		T value = shrinkable.value();
		List<T> elements = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return checkValueUniqueIn(extractors, value, elements);
	}

	public static <T> boolean checkValueUniqueIn(Collection<? extends FeatureExtractor<T>> extractors, T value, Collection<? extends T> elements) {
		for (FeatureExtractor<T> extractor : extractors) {
			if (!extractor.isUniqueIn(value, elements)) {
				return false;
			}
		}
		return true;
	}

	public static <T extends @Nullable Object> boolean checkUniquenessOfShrinkables(Collection<? extends FeatureExtractor<T>> extractors, List<? extends Shrinkable<T>> shrinkables) {
		if (extractors.isEmpty()) {
			return true;
		}
		List<T> elements = shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
		return checkUniquenessOfValues(extractors, elements);
	}

	public static <T extends @Nullable Object> boolean checkUniquenessOfValues(Collection<? extends FeatureExtractor<T>> extractors, Collection<? extends T> elements) {
		for (FeatureExtractor<T> extractor : extractors) {
			if (!extractor.areUnique(elements)) {
				return false;
			}
		}
		return true;
	}

}
