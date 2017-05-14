package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

public interface Shrinkable<T> {

	Optional<ShrinkResult<T>> shrink(Predicate<T> falsifier);

	default Shrinkable<T> filter(Predicate<T> filterPredicate) {
		return this;
	}

	static <T> Shrinkable<T> empty() {
		return falsifier -> Optional.empty();
	}

}
