package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

public interface Shrinkable<T> {

	Optional<ShrinkResult<T>> shrink(Predicate<T> falsifier);

	static <T> Shrinkable<T> empty() {
		return falsifier -> Optional.empty();
	}
}
