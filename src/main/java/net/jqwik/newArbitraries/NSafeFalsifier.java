package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NSafeFalsifier {

	public static <T> Optional<NShrinkResult<NShrinkable<T>>> falsify(Predicate<T> falsifier, NShrinkable<T> shrinkable) {
		Predicate<NShrinkable<T>> shrinkableFalsifier = s -> falsifier.test(s.value());
		return falsify(shrinkableFalsifier, shrinkable);
	}

	public static <T> Optional<NShrinkResult<T>> falsify(Predicate<T> falsifier, T value) {
		try {
			if (falsifier.negate().test(value)) {
				return Optional.of(NShrinkResult.of(value, null));
			}
		} catch (Throwable error) {
			return Optional.of(NShrinkResult.of(value, error));
		}
		return Optional.empty();
	}
}
