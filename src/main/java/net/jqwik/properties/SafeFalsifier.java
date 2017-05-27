package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

public class SafeFalsifier {

	public static <T> Optional<ShrinkResult<Shrinkable<T>>> falsify(Predicate<T> falsifier, Shrinkable<T> shrinkable) {
		Predicate<Shrinkable<T>> shrinkableFalsifier = s -> falsifier.test(s.value());
		return falsify(shrinkableFalsifier, shrinkable);
	}

	public static <T> Optional<ShrinkResult<T>> falsify(Predicate<T> falsifier, T value) {
		try {
			if (falsifier.negate().test(value)) {
				return Optional.of(ShrinkResult.of(value, null));
			}
		} catch (Throwable error) {
			return Optional.of(ShrinkResult.of(value, error));
		}
		return Optional.empty();
	}
}
