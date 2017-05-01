package net.jqwik.properties;

import net.jqwik.properties.shrinking.*;

import java.util.*;
import java.util.function.*;

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

		AssertionError lastFalsifiedError = originalError;
		List<Object> lastFalsifiedParams = new ArrayList<>(originalParams);
		for (int i = 0; i < originalParams.size(); i++) {
			shrinkPosition(i, lastFalsifiedParams);
		}
		return Result.of(lastFalsifiedParams, lastFalsifiedError);
	}

	public void shrinkPosition(int position, List<Object> lastFalsifiedParams) {
		Arbitrary currentArbitrary = arbitraries.get(position);
		Object currentParam = lastFalsifiedParams.get(position);
		Predicate<Object> falsifier = createFalsifierForPosition(position, lastFalsifiedParams);
		ShrinkTree<Object> shrinkTree = currentArbitrary.shrink(currentParam);
		List<ShrinkResult<Object>> shrinkResults = shrinkTree.falsify(falsifier);
		shrinkResults.stream() //
			.sorted(Comparator.naturalOrder()) //
			.findFirst() //
			.ifPresent(shrinkResult -> lastFalsifiedParams.set(position, shrinkResult.value()));
	}

	private Predicate<Object> createFalsifierForPosition(int position, List<Object> lastFalsifiedParams) {
		return param -> {
			List<Object> effectiveParams = new ArrayList<>(lastFalsifiedParams);
			effectiveParams.set(position, param);
			return forAllFunction.apply(effectiveParams);
		};
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
