package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.properties.*;

public class ShrinkableList<T> implements Shrinkable<List<T>> {

	private final Shrinkable<List<T>> underlying;
	private final Arbitrary<T> elementArbitrary;

	public ShrinkableList(Shrinkable<List<T>> underlying, Arbitrary<T> elementArbitrary) {
		this.underlying = underlying;
		this.elementArbitrary = elementArbitrary;
	}

	@Override
	public Optional<ShrinkResult<List<T>>> shrink(Predicate<List<T>> falsifier) {
		return underlying.shrink(falsifier).map(shrinkResult -> shrinkElements(shrinkResult, falsifier));
	}

	public Shrinkable<List<T>> underlying() {
		return underlying;
	}

	private ShrinkResult<List<T>> shrinkElements(ShrinkResult<List<T>> shrinkResult, Predicate<List<T>> falsifier) {
		ParameterListShrinker<T> parameterListShrinker = new ParameterListShrinker<>(falsifier, ignore -> elementArbitrary);
		return parameterListShrinker.shrinkListElements( //
				shrinkResult.value(), //
				shrinkResult.error().orElse(null) //
		);
	}


}
