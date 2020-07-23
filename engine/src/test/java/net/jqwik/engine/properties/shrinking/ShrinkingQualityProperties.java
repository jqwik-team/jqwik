package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

/**
 * Inspired by https://github.com/HypothesisWorks/hypothesis/blob/master/hypothesis-python/tests/quality/test_shrink_quality.py
 * but still a lot of that missing
 */
class ShrinkingQualityProperties {

	@Property
	@ExpectFailure(checkResult = ShrinkTo2Elements.class)
	void reversingAList(@ForAll List<Integer> ls) {
		assertThat(reversed(ls)).isEqualTo(ls);
	}

	private class ShrinkTo2Elements implements Consumer<PropertyExecutionResult> {
		@Override
		public void accept(final PropertyExecutionResult propertyExecutionResult) {
			@SuppressWarnings("unchecked")
			List<Integer> shrunkResult = (List<Integer>) propertyExecutionResult.falsifiedParameters().get().get(0);

			// result can be [0, 1] or [0, -1] or other way round
			assertThat(shrunkResult).hasSize(2);
			assertThat(shrunkResult).contains(0);
			assertThat(shrunkResult).containsAnyOf(0, 1, -1);
		}
	}

	@Property(tries = 100)
	void reversingAList(@ForAll Random random) {
		Arbitrary<List<Integer>> integerLists = Arbitraries.integers().list();

		TestingFalsifier<List<Integer>> reverseEqualsOriginal = falsifier((List<Integer> list) -> list.equals(reversed(list)));
		List<Integer> shrunkResult = falsifyThenShrink(integerLists, random, reverseEqualsOriginal);

		// result can be [0, 1] or [0, -1] or other way round
		assertThat(shrunkResult).hasSize(2);
		assertThat(shrunkResult).contains(0);
		assertThat(shrunkResult).containsAnyOf(0, 1, -1);
	}

	private List<Integer> reversed(final List<Integer> ls) {
		ArrayList<Integer> reversed = new ArrayList<>(ls);
		Collections.reverse(reversed);
		return reversed;
	}

	@Property(tries = 100)
	void largeUnionList(@ForAll Random random) {
		ListArbitrary<List<Integer>> listOfLists = Arbitraries.integers().list().list();

		TestingFalsifier<List<List<Integer>>> containsLessThan5DistinctNumbers = falsifier((List<List<Integer>> ls) -> {
			Set<Integer> allElements = new HashSet<>();
			for (List<Integer> x : ls) {
				allElements.addAll(x);
			}
			return allElements.size() < 5;
		});
		List<List<Integer>> shrunkResult = falsifyThenShrink(listOfLists, random, containsLessThan5DistinctNumbers);

		// e.g. [[1, 2, 3, 4, 5]]
		int numberOfElements = shrunkResult.stream().mapToInt(List::size).sum();
		// TODO: Better shrinking should certify that:
		//assertThat(shrunkResult).hasSize(1);
		//assertThat(numberOfElements).isEqualTo(5);
		assertThat(numberOfElements).isLessThanOrEqualTo(10);
	}

	@Property(tries = 100)
	void flatMapRectangles(@ForAll Random random) {
		Arbitrary<Integer> lengths = Arbitraries.integers().between(0, 10);
		List<String> shrunkResult = falsifyThenShrink(
			lengths.flatMap(this::listsOfLength),
			random,
			falsifier(x -> !x.equals(Arrays.asList("a", "b")))
		);

		assertThat(shrunkResult).containsExactly("a", "b");
	}

	private ListArbitrary<String> listsOfLength(int n) {
		return Arbitraries.of("a", "b").list().ofSize(n);
	}
}
