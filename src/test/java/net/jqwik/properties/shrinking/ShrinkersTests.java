package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.stream.*;

public class ShrinkersTests {

	@Example
	void shrinkTreeForBoundedIntRange() {

		Shrinker<Integer> shrinker = Shrinkers.range(-10, 10);
		ShrinkTree<Integer> shrinkTree = shrinker.shrink(0);

		Stream<ShrinkValue<Integer>> shrinkingStream = shrinkTree.stream();

	}
}
