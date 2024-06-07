package net.jqwik.engine.properties;

import org.jspecify.annotations.*;

import java.util.*;
import java.util.function.*;

@FunctionalInterface
public interface FeatureExtractor<T extends @Nullable Object> extends Function<T, Object> {

	static <T extends @Nullable Object> FeatureExtractor<T> identity() {
		return t -> t;
	}

	default Object applySafe(T t) {
		try {
			return apply(t);
		} catch (NullPointerException npe) {
			return null;
		}
	}

	default boolean isUniqueIn(T value, Collection<? extends T> elements) {
		if (this == identity()) {
			return !elements.contains(value);
		}
		Object feature = applySafe(value);
		return elements.stream()
					   .map(this::applySafe)
					   .noneMatch(x -> Objects.equals(x, feature));
	}

	default boolean areUnique(Collection<? extends T> elements) {
		Set<Object> set = new HashSet<>();
		for (T x : elements) {
			if (!set.add(applySafe(x))) {
				return false;
			}
		}
		return true;
	}
}
