package net.jqwik.properties.shrinking;

import net.jqwik.properties.*;

import java.util.*;
import java.util.function.*;

public class ShrinkableList<T> implements Shrinkable<List<T>> {

	private final Shrinkable<List<T>> underlying;
	private final Arbitrary<T> elementArbitrary;

	public ShrinkableList(Shrinkable<List<T>> underlying, Arbitrary<T> elementArbitrary) {
		this.underlying = underlying;
		this.elementArbitrary = elementArbitrary;
	}

	@Override
	public Optional<ShrinkResult<List<T>>> shrink(Predicate<List<T>> falsifier) {
		return underlying().shrink(falsifier);
	}

	public Shrinkable<List<T>> underlying() {
		return underlying;
	}
}
