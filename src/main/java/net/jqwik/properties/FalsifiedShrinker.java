package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

public class FalsifiedShrinker {

	private final List<Arbitrary> arbitraries;
	private final Function<List<Object>, Boolean> forAllFunction;

	public FalsifiedShrinker(List<Arbitrary> arbitraries, Function<List<Object>, Boolean> forAllFunction) {
		this.arbitraries = arbitraries;
		this.forAllFunction = forAllFunction;
	}

	// TODO: Really shrink the params using arbitraries' shrink method
	public Result shrink(List<Object> originalParams, AssertionError originalError) {
		return Result.of(originalParams, originalError);
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
