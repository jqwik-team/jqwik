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

	public Result shrink(List<Object> originalParams, AssertionError originalError) {
		if (originalParams.isEmpty())
			return Result.of(originalParams, originalError);

		Predicate<List<Object>> forAllFalsifier = forAllFunction::apply;
		ParameterListShrinker<Object> parameterListShrinker = new ParameterListShrinker<Object>(forAllFalsifier, position -> arbitraries.get(position));

		ShrinkResult<List<Object>> shrinkResult = parameterListShrinker.shrinkListElements(originalParams, Optional.ofNullable(originalError), 0);

		return Result.of(shrinkResult.value(), shrinkResult.error().orElse(null));
	}

	public static class Result {

		public static Result of(List<Object> params, AssertionError assertionError) {
			return new Result(params, assertionError);
		}

		private final List<Object> params;
		private final AssertionError assertionError;

		private Result(List<Object> params, AssertionError assertionError) {
			this.params = params;
			this.assertionError = assertionError;
		}

		public List<Object> params() {
			return params;
		}

		public AssertionError error() {
			return assertionError;
		}

	}
}
