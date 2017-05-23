package net.jqwik.newArbitraries;

import net.jqwik.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class NListShrinkerTests {

	private NListShrinker<Integer> shrinker = new NListShrinker<>();

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
		assertThat(shrinker.nextShrinkingCandidates(new ArrayList<>())).isEmpty();
	}

	@Example
	void stringOfLength1IsShrunkToEmpty() {
		assertThat(shrinker.nextShrinkingCandidates(NArbitraryTestHelper.listOfShrinkableIntegers(1))).containsExactly(Collections.emptyList());
	}

	@Example
	void longerStringsAreShrunkFromBothSides() {
		assertThat(shrinker.nextShrinkingCandidates(NArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3, 4))).containsExactly(
			NArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3),
			NArbitraryTestHelper.listOfShrinkableIntegers(2, 3, 4)
		);
	}

}
