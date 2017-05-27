package net.jqwik.newArbitraries;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class NParameterListShrinkerTests {

	@Group
	class Shrink {
		@Example
		void shrinkToEnd() {
			List<NShrinkable<Integer>> shrinkableValues = NArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3);
			NParameterListShrinker<Integer> listShrinker = new NParameterListShrinker<>(shrinkableValues);

			Predicate<List<Integer>> falsifier = params -> false;
			NShrinkResult<List<NShrinkable<Integer>>> shrinkResult = listShrinker.shrink(falsifier, null);

			assertThat(extractParams(shrinkResult)).containsExactly(0, 0, 0);
			assertThat(shrinkResult.throwable()).isNotPresent();
		}

		@Example
		void keepLastErrorWhenShrinking() {
			List<NShrinkable<Integer>> shrinkableValues = NArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3);
			NParameterListShrinker<Integer> listShrinker = new NParameterListShrinker<>(shrinkableValues);

			AssertionError error = new AssertionError("test");
			Predicate<List<Integer>> falsifier = params -> {
				throw error;
			};
			NShrinkResult<List<NShrinkable<Integer>>> shrinkResult = listShrinker.shrink(falsifier, null);

			assertThat(extractParams(shrinkResult)).containsExactly(0, 0, 0);
			assertThat(shrinkResult.throwable()).isPresent();
			assertThat(shrinkResult.throwable().get()).isSameAs(error);
		}

		@Example
		void shrinkPartially() {
			List<NShrinkable<Integer>> shrinkableValues = NArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3);
			NParameterListShrinker<Integer> listShrinker = new NParameterListShrinker<>(shrinkableValues);

			Predicate<List<Integer>> falsifier = params -> params.stream().anyMatch(anInt -> anInt == 0);
			NShrinkResult<List<NShrinkable<Integer>>> shrinkResult = listShrinker.shrink(falsifier, null);

			assertThat(extractParams(shrinkResult)).containsExactly(1, 1, 1);
			assertThat(shrinkResult.throwable()).isNotPresent();
		}

		private List<Integer> extractParams(NShrinkResult<List<NShrinkable<Integer>>> shrinkResult) {
			return shrinkResult.shrunkValue().stream().map(NShrinkable::value).collect(Collectors.toList());
		}
	}

	@Group
	class ShrinkNext {
		@Example
		void noNextShrinkIfNoParameterCanBeShrunk() {
			List<NShrinkable<Integer>> unshrinkableValues = Arrays.asList( //
					NShrinkable.unshrinkable(1), //
					NShrinkable.unshrinkable(2), //
					NShrinkable.unshrinkable(3));
			NParameterListShrinker<Integer> listShrinker = new NParameterListShrinker<>(unshrinkableValues);

			Set<NShrinkResult<List<NShrinkable<Integer>>>> shrinkResults = listShrinker.shrinkNext(MockFalsifier.falsifyAll());
			assertThat(shrinkResults).isEmpty();
		}

		@Example
		void singleShrinkableElementIsShrunkOnlyOneStep() {
			List<NShrinkable<Integer>> shrinkableValues = NArbitraryTestHelper.listOfShrinkableIntegers(2, 0, 0);
			NParameterListShrinker<Integer> listShrinker = new NParameterListShrinker<>(shrinkableValues);

			Set<NShrinkResult<List<NShrinkable<Integer>>>> shrinkResults = listShrinker.shrinkNext(MockFalsifier.falsifyAll());
			Set<List<Integer>> results = extractResults(shrinkResults);

			assertThat(results).hasSize(1);
			assertThat(results.iterator().next()).containsExactly(1, 0, 0);
		}

		@Example
		void onlyFirstShrinkableElementIsShrunk() {
			List<NShrinkable<Integer>> shrinkableValues = NArbitraryTestHelper.listOfShrinkableIntegers(0, 2, 2);
			NParameterListShrinker<Integer> listShrinker = new NParameterListShrinker<>(shrinkableValues);

			Set<NShrinkResult<List<NShrinkable<Integer>>>> shrinkResults = listShrinker.shrinkNext(MockFalsifier.falsifyAll());
			Set<List<Integer>> results = extractResults(shrinkResults);

			assertThat(results).hasSize(1);
			assertThat(results.iterator().next()).containsExactly(0, 1, 2);
		}

	}

	private Set<List<Integer>> extractResults(Set<NShrinkResult<List<NShrinkable<Integer>>>> shrinkResults) {
		return shrinkResults.stream() //
				.map(shrinkResult -> shrinkResult.shrunkValue() //
						.stream() //
						.map(NShrinkable::value) //
						.collect(Collectors.toList())) //
				.collect(Collectors.toSet());
	}
}
