package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

import net.jqwik.properties.shrinking.*;

public class FalsifiedShrinker {

	private final List<Arbitrary> arbitraries;
	private final Function<List<Object>, Boolean> forAllFunction;

	public FalsifiedShrinker(List<Arbitrary> arbitraries, Function<List<Object>, Boolean> forAllFunction) {
		this.arbitraries = arbitraries;
		this.forAllFunction = forAllFunction;
	}

	public ShrinkResult<List<Object>> shrink(List<Object> originalParams, AssertionError originalError) {
		Predicate<List<Object>> forAllFalsifier = forAllFunction::apply;
		ParameterListShrinker<Object> parameterListShrinker = new ParameterListShrinker<>(forAllFalsifier,
				position -> arbitraries.get(position));

		return parameterListShrinker.shrinkListElements(originalParams, Optional.ofNullable(originalError), 0);
	}

}
