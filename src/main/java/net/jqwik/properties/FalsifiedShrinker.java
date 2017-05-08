package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

import net.jqwik.properties.shrinking.*;

public class FalsifiedShrinker {

	private final List<Arbitrary> arbitraries;
	private final Predicate<List<Object>> forAllPredicate;

	public FalsifiedShrinker(List<Arbitrary> arbitraries, Predicate<List<Object>> forAllPredicate) {
		this.arbitraries = arbitraries;
		this.forAllPredicate = forAllPredicate;
	}

	public ShrinkResult<List<Object>> shrink(List<Object> originalParams, AssertionError originalError) {
		ParameterListShrinker<Object> parameterListShrinker = new ParameterListShrinker<>(forAllPredicate,
				position -> arbitraries.get(position));

		return parameterListShrinker.shrinkListElements(originalParams, originalError);
	}

}
