package net.jqwik.properties.shrinking;

import static org.assertj.core.api.Assertions.*;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

public class ListShrinkingTests {

	@Example
	void shrinkFromEmptyListReturnsNothing() {
		Shrinker<List<Integer>> shrinker = Shrinkers.list(new IntegerArbitrary(-5, 5));
		ShrinkableList<Integer> shrinkableChoice = (ShrinkableList<Integer>) shrinker.shrink(Collections.emptyList());

		assertThat(shrinkableChoice.steps()).hasSize(0);
	}

	@Example
	void shrinkingSizeOfListFirst() {
		Shrinker<List<Integer>> shrinker = Shrinkers.list(new IntegerArbitrary(-5, 5));
		List<Integer> listOf6 = Arrays.asList(1, 2, 3, 4, 5, 6);
		ShrinkableList<Integer> shrinkableChoice = (ShrinkableList<Integer>) shrinker.shrink(listOf6);

		assertThat(shrinkableChoice.steps()).hasSize(7);
		assertThat(shrinkableChoice.steps()).containsExactly( //
				shrinkableValueOfList(1, 2, 3, 4, 5, 6), //
				shrinkableValueOfList(1, 2, 3, 4, 5), //
				shrinkableValueOfList(1, 2, 3, 4), //
				shrinkableValueOfList(1, 2, 3), //
				shrinkableValueOfList(1, 2), //
				shrinkableValueOfList(1), //
				shrinkableValueOfList() //
		);
	}

	private ShrinkableValue<List<Integer>> shrinkableValueOfList(int ... values) {
		return ShrinkableValue.of(new ArrayList<Integer>(), values.length);
	}

}
