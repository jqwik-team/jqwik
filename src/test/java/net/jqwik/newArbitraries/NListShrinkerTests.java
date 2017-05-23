package net.jqwik.newArbitraries;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

class NListShrinkerTests {

	private NListShrinker<Integer> shrinker = new NListShrinker<>();

	@Example
	void distanceIsLengthPlusDistanceOfElements() {
		assertThat(shrinker.distance(new ArrayList<>())).isEqualTo(0);

		// Just length since elements have distance of 0
		assertThat(shrinker.distance(list(0))).isEqualTo(1);
		assertThat(shrinker.distance(list(0, 0, 0))).isEqualTo(3);

		// Length plus sum of all distances
		assertThat(shrinker.distance(list(1))).isEqualTo(2);
		assertThat(shrinker.distance(list(1, 2, 3))).isEqualTo(9);
	}

	@Example
	void emptyListIsNotShrunk() {
		assertThat(shrinker.shrink(new ArrayList<>())).isEmpty();
	}

	@Example
	void stringOfLength1IsShrunkToEmpty() {
		assertThat(shrinker.shrink(list(1))).containsExactly(Collections.emptyList());
	}

	@Example
	void longerStringsAreShrunkFromBothSides() {
		assertThat(shrinker.shrink(list(1, 2, 3, 4))).containsExactly(
			list(1, 2, 3),
			list(2, 3, 4)
		);
	}


	private List<NShrinkable<Integer>> list(int... numbers) {
		NShrinker<Integer> numberShrinker = new NShrinker<Integer>() {
			@Override
			public Set<Integer> shrink(Integer value) {
				if (value == 0) return Collections.emptySet();
				return Collections.singleton(value - 1);
			}

			@Override
			public int distance(Integer value) {
				return value;
			}
		};
		return Arrays.stream(numbers) //
			.mapToObj(anInt -> new NShrinkableValue<>(anInt, numberShrinker)) //
			.collect(Collectors.toList());
	}

}
