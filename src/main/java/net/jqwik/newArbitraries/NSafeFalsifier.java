package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NSafeFalsifier {

	public static <T> Optional<NShrinkResult<NShrinkable<T>>> falsify(Predicate<T> falsifier, NShrinkable<T> shrinkable) {
		try {
			if (shrinkable.falsifies(falsifier)) {
				return Optional.of(NShrinkResult.of(shrinkable, null));
			}
		} catch (Throwable error) {
			return  Optional.of(NShrinkResult.of(shrinkable, error));
		}
		return Optional.empty();
	}
}
