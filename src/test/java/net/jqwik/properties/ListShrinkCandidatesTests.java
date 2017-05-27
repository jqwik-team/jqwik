package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class ListShrinkCandidatesTests {

	private ListShrinkCandidates<Integer> shrinker = new ListShrinkCandidates<>();

	@Example
	void distanceIsLengthPlusDistanceOfElements() {
		assertThat(shrinker.distance(new ArrayList<>())).isEqualTo(0);

		// Just length since elements have distance of 0
		assertThat(shrinker.distance(ArbitraryTestHelper.listOfShrinkableIntegers(0))).isEqualTo(1);
		assertThat(shrinker.distance(ArbitraryTestHelper.listOfShrinkableIntegers(0, 0, 0))).isEqualTo(3);

		// Length plus sum of all distances
		assertThat(shrinker.distance(ArbitraryTestHelper.listOfShrinkableIntegers(1))).isEqualTo(2);
		assertThat(shrinker.distance(ArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3))).isEqualTo(9);
	}

	@Example
	void emptyListIsNotShrunk() {
		assertThat(shrinker.nextCandidates(new ArrayList<>())).isEmpty();
	}

	@Example
	void stringOfLength1IsShrunkToEmpty() {
		assertThat(shrinker.nextCandidates(ArbitraryTestHelper.listOfShrinkableIntegers(1))).containsExactly(Collections.emptyList());
	}

	@Example
	void longerStringsAreShrunkFromBothSides() {
		assertThat(shrinker.nextCandidates(ArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3, 4))).containsExactly(
			ArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3),
			ArbitraryTestHelper.listOfShrinkableIntegers(2, 3, 4)
		);
	}

}
