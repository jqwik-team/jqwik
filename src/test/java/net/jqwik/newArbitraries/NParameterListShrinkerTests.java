package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;

import static org.assertj.core.api.Assertions.assertThat;

public class NParameterListShrinkerTests {

	@Example
	void noNextShrinkIfNoParameterCanBeShrunk() {
		Predicate<List<Integer>> forAllFalsifier = MockFalsifier.falsifyAll();
		NParameterListShrinker<Integer> listShrinker = new NParameterListShrinker<>(forAllFalsifier);

		List<NShrinkable<Integer>> unshrinkableValues = Arrays.asList(NShrinkableValue.unshrinkable(1), NShrinkableValue.unshrinkable(2),
				NShrinkableValue.unshrinkable(3));

		Set<NShrinkResult<List<NShrinkable<Integer>>>> shrinkResults = listShrinker.shrinkNext(unshrinkableValues);
		assertThat(shrinkResults).isEmpty();
	}

	@Example
	void singleShrinkableElementIsShrunkOnlyOneStep() {
		Predicate<List<Integer>> forAllFalsifier = MockFalsifier.falsifyAll();
		NParameterListShrinker<Integer> listShrinker = new NParameterListShrinker<>(forAllFalsifier);

		List<NShrinkable<Integer>> shrinkableValues = NArbitraryTestHelper.listOfShrinkableIntegers(2, 0, 0);

		Set<NShrinkResult<List<NShrinkable<Integer>>>> shrinkResults = listShrinker.shrinkNext(shrinkableValues);
		Set<List<Integer>> results = extractResults(shrinkResults);

		assertThat(results).hasSize(1);
		assertThat(results.iterator().next()).containsExactly(1, 0, 0);
	}

	private Set<List<Integer>> extractResults(Set<NShrinkResult<List<NShrinkable<Integer>>>> shrinkResults) {
		return shrinkResults.stream() //
				.map(shrinkResult -> shrinkResult.shrunkValue() //
						.stream() //
						.map(shrinkable -> shrinkable.value()) //
						.collect(Collectors.toList())) //
				.collect(Collectors.toSet());
	}
}
