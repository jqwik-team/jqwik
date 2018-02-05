package net.jqwik.properties;

import net.jqwik.api.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

class ParameterListShrinkerTests {

	@Group
	class Shrink {
		@Example
		void shrinkToEnd() {
			List<Shrinkable<Integer>> shrinkableValues = ArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3);
			ParameterListShrinker<Integer> listShrinker = new ParameterListShrinker<>(shrinkableValues, e -> {}, new Reporting[0]);

			Predicate<List<Integer>> falsifier = params -> false;
			ShrinkResult<List<Shrinkable<Integer>>> shrinkResult = listShrinker.shrink(falsifier, null);

			assertThat(extractParams(shrinkResult)).containsExactly(0, 0, 0);
			assertThat(shrinkResult.throwable()).isNotPresent();
		}

		@Example
		void keepLastErrorWhenShrinking() {
			List<Shrinkable<Integer>> shrinkableValues = ArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3);
			ParameterListShrinker<Integer> listShrinker = new ParameterListShrinker<>(shrinkableValues, e -> {}, new Reporting[0]);

			AssertionError error = new AssertionError("test");
			Predicate<List<Integer>> falsifier = params -> {
				throw error;
			};
			ShrinkResult<List<Shrinkable<Integer>>> shrinkResult = listShrinker.shrink(falsifier, null);

			assertThat(extractParams(shrinkResult)).containsExactly(0, 0, 0);
			assertThat(shrinkResult.throwable()).isPresent();
			assertThat(shrinkResult.throwable().get()).isSameAs(error);
		}

		@Example
		void shrinkPartially() {
			List<Shrinkable<Integer>> shrinkableValues = ArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3);
			ParameterListShrinker<Integer> listShrinker = new ParameterListShrinker<>(shrinkableValues, e -> {}, new Reporting[0]);

			Predicate<List<Integer>> falsifier = params -> params.stream().anyMatch(anInt -> anInt == 0);
			ShrinkResult<List<Shrinkable<Integer>>> shrinkResult = listShrinker.shrink(falsifier, null);

			assertThat(extractParams(shrinkResult)).containsExactly(1, 1, 1);
			assertThat(shrinkResult.throwable()).isNotPresent();
		}

		private List<Integer> extractParams(ShrinkResult<List<Shrinkable<Integer>>> shrinkResult) {
			return shrinkResult.shrunkValue().stream().map(Shrinkable::value).collect(Collectors.toList());
		}
	}

	@Group
	class ShrinkNext {
		@Example
		void noNextShrinkIfNoParameterCanBeShrunk() {
			List<Shrinkable<Integer>> unshrinkableValues = Arrays.asList( //
																		  Shrinkable.unshrinkable(1), //
																		  Shrinkable.unshrinkable(2), //
																		  Shrinkable.unshrinkable(3));
			ParameterListShrinker<Integer> listShrinker = new ParameterListShrinker<>(unshrinkableValues, e -> {}, new Reporting[0]);

			Set<ShrinkResult<List<Shrinkable<Integer>>>> shrinkResults = listShrinker.shrinkNext(MockFalsifier.falsifyAll());
			assertThat(shrinkResults).isEmpty();
		}

		@Example
		void singleShrinkableElementIsShrunkOnlyOneStep() {
			List<Shrinkable<Integer>> shrinkableValues = ArbitraryTestHelper.listOfShrinkableIntegers(2, 0, 0);
			ParameterListShrinker<Integer> listShrinker = new ParameterListShrinker<>(shrinkableValues, e -> {}, new Reporting[0]);

			Set<ShrinkResult<List<Shrinkable<Integer>>>> shrinkResults = listShrinker.shrinkNext(MockFalsifier.falsifyAll());
			Set<List<Integer>> results = extractResults(shrinkResults);

			assertThat(results).hasSize(1);
			assertThat(results.iterator().next()).containsExactly(1, 0, 0);
		}

		@Example
		void onlyFirstShrinkableElementIsShrunk() {
			List<Shrinkable<Integer>> shrinkableValues = ArbitraryTestHelper.listOfShrinkableIntegers(0, 2, 2);
			ParameterListShrinker<Integer> listShrinker = new ParameterListShrinker<>(shrinkableValues, e -> {}, new Reporting[0]);

			Set<ShrinkResult<List<Shrinkable<Integer>>>> shrinkResults = listShrinker.shrinkNext(MockFalsifier.falsifyAll());
			Set<List<Integer>> results = extractResults(shrinkResults);

			assertThat(results).hasSize(1);
			assertThat(results.iterator().next()).containsExactly(0, 1, 2);
		}

	}

	private Set<List<Integer>> extractResults(Set<ShrinkResult<List<Shrinkable<Integer>>>> shrinkResults) {
		return shrinkResults.stream() //
				.map(shrinkResult -> shrinkResult.shrunkValue() //
												 .stream() //
												 .map(Shrinkable::value) //
												 .collect(Collectors.toList())) //
				.collect(Collectors.toSet());
	}
}
