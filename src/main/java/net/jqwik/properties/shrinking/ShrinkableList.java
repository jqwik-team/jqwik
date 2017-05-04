package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

public class ShrinkableList<T> extends ShrinkableChoice<List<T>> {

	@Override
	public Optional<ShrinkResult<List<T>>> shrink(Predicate<List<T>> falsifier) {
		return null;
	}
}
