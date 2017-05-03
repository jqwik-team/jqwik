package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class ListShrinkingTests {

	@Example
	void shrinkFromEmptyListReturnsNothing() {
		Shrinker<List<Integer>> shrinker = Shrinkers.list(new IntegerShrinker(-5, 5));
		ShrinkTree<List<Integer>> shrinkTree = shrinker.shrink(Collections.emptyList());

		assertThat(shrinkTree.shrinkingRoutes()).hasSize(0);
	}

//	@Example
//	void shrinkingSizeOfListFirst() {
//		Shrinker<List<Integer>> shrinker = Shrinkers.list(new IntegerShrinker(-5, 5));
//		List<Integer> listOf6 = Arrays.asList(0, 0, 0, 0, 0, 0);
//		ShrinkTree<List<Integer>> shrinkTree = shrinker.shrink(listOf6);
//
//		assertThat(shrinkTree.shrinkingRoutes()).hasSize(0);
//	}

}
