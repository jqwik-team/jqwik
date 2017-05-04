package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

public class LazyShrinkable<T> implements Shrinkable<T> {

	@Override
	public Optional<ShrinkResult<T>> shrink(Predicate<T> falsifier) {
		return null;
	}
}
