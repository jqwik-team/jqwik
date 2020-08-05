package net.jqwik.engine.properties.shrinking;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

class LazyArbitraryShrinkingTests {

	@Property(tries = 10)
	void oneStep(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			Arbitraries.lazy(() -> Arbitraries.of(1, 2, 3, 4, 5, 6));
		Integer value = shrinkToMinimal(arbitrary, random);
		Assertions.assertThat(value).isEqualTo((Integer) 1);
	}

	@Property(tries = 10)
	void severalStepsToList(@ForAll Random random) {
		Arbitrary<List<Integer>> arbitrary = listOfInteger();
		TestingFalsifier<List<Integer>> falsifier = integers -> integers.size() < 2;
		List<Integer> shrunkValue = falsifyThenShrink(arbitrary, random, falsifier);

		Assertions.assertThat(shrunkValue).isEqualTo(Arrays.asList(1, 1));
	}

	@Provide
	Arbitrary<List<Integer>> listOfInteger() {
		Arbitrary<List<Integer>> lazyList = Arbitraries.lazy(this::listOfInteger);

		return Arbitraries.oneOf(
			Arbitraries.of(1, 2, 3, 4, 5).list().ofSize(1),
			Combinators.combine(lazyList, lazyList).as((l1, l2) -> {
				ArrayList<Integer> newList = new ArrayList<>(l1);
				newList.addAll(l2);
				return newList;
			})
		);
	}

	@Disabled("Shrinking never stops")
	/**
	 * Reversed order in oneOf() leads to endless shrinking
	 */
	@Property(tries = 10)
	void severalStepsToListReversedLazy(@ForAll Random random) {
		Arbitrary<List<Integer>> arbitrary = listOfIntegerReversedLazy();
		TestingFalsifier<List<Integer>> falsifier = integers -> integers.size() < 2;
		List<Integer> shrunkValue = falsifyThenShrink(arbitrary, random, falsifier);

		Assertions.assertThat(shrunkValue).isEqualTo(Arrays.asList(1, 1));
	}

	@Provide
	Arbitrary<List<Integer>> listOfIntegerReversedLazy() {
		Arbitrary<List<Integer>> lazyList = Arbitraries.lazy(this::listOfIntegerReversedLazy);

		return Arbitraries.oneOf(
			Combinators.combine(lazyList, lazyList).as((l1, l2) -> {
				ArrayList<Integer> newList = new ArrayList<>(l1);
				newList.addAll(l2);
				return newList;
			}),
			Arbitraries.of(1, 2, 3, 4, 5).list().ofSize(1)
		);
	}

}
