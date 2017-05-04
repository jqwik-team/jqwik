package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class ListShrinkingTests {

	@Example
	void shrinkFromEmptyListReturnsNothing() {
		Shrinker<List<Integer>> shrinker = Shrinkers.list(new IntegerShrinker(-5, 5));
		ShrinkableChoice<List<Integer>> shrinkableChoice = (ShrinkableChoice<List<Integer>>) shrinker.shrink(Collections.emptyList());

		assertThat(shrinkableChoice.choices()).hasSize(0);
	}

	@Example
	void shrinkingSizeOfListFirst() {
		Shrinker<List<Integer>> shrinker = Shrinkers.list(new IntegerShrinker(-5, 5));
		List<Integer> listOf6 = Arrays.asList(1, 2, 3, 4, 5, 6);
		ShrinkableList<Integer> shrinkableChoice = (ShrinkableList<Integer>) shrinker.shrink(listOf6);

		assertThat(shrinkableChoice.choices()).hasSize(4);
		assertThat(shrinkableChoice.choices()).containsExactly(
			shrinkableValueOfList(1,2,3,4,5,6),
			shrinkableValueOfList(1, 2, 3),
			shrinkableValueOfList(1),
			shrinkableValueOfList(0)
		);
	}

	private ShrinkableValue<List<Integer>> shrinkableValueOfList(int ... values) {
		return ShrinkableValue.of(new ArrayList<Integer>(), 0);
	}

}
