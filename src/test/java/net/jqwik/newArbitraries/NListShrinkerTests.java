package net.jqwik.newArbitraries;

import net.jqwik.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class NListShrinkerTests {

	private NListShrinkCandidates<Integer> shrinker = new NListShrinkCandidates<>();

	@Example
	void distanceIsLengthPlusDistanceOfElements() {
		assertThat(shrinker.distance(new ArrayList<>())).isEqualTo(0);

		// Just length since elements have distance of 0
		assertThat(shrinker.distance(NArbitraryTestHelper.listOfShrinkableIntegers(0))).isEqualTo(1);
		assertThat(shrinker.distance(NArbitraryTestHelper.listOfShrinkableIntegers(0, 0, 0))).isEqualTo(3);

		// Length plus sum of all distances
		assertThat(shrinker.distance(NArbitraryTestHelper.listOfShrinkableIntegers(1))).isEqualTo(2);
		assertThat(shrinker.distance(NArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3))).isEqualTo(9);
	}

	@Example
	void emptyListIsNotShrunk() {
		assertThat(shrinker.nextCandidates(new ArrayList<>())).isEmpty();
	}

	@Example
	void stringOfLength1IsShrunkToEmpty() {
		assertThat(shrinker.nextCandidates(NArbitraryTestHelper.listOfShrinkableIntegers(1))).containsExactly(Collections.emptyList());
	}

	@Example
	void longerStringsAreShrunkFromBothSides() {
		assertThat(shrinker.nextCandidates(NArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3, 4))).containsExactly(
			NArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3),
			NArbitraryTestHelper.listOfShrinkableIntegers(2, 3, 4)
		);
	}

}
