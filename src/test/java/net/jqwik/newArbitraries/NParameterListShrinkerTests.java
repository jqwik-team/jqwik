package net.jqwik.newArbitraries;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;

public class NParameterListShrinkerTests {

	@Example
	void noNextShrinkIfNoParameterCanBeShrunk() {
		List<NShrinkable<Integer>> unshrinkableValues = Arrays.asList( //
																	   NShrinkable.unshrinkable(1), //
																	   NShrinkable.unshrinkable(2), //
																	   NShrinkable.unshrinkable(3)
		);
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

	private Set<List<Integer>> extractResults(Set<NShrinkResult<List<NShrinkable<Integer>>>> shrinkResults) {
		return shrinkResults.stream() //
				.map(shrinkResult -> shrinkResult.shrunkValue() //
						.stream() //
						.map(NShrinkable::value) //
						.collect(Collectors.toList())) //
				.collect(Collectors.toSet());
	}
}
