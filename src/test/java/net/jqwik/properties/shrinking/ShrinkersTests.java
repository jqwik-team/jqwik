package net.jqwik.properties.shrinking;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import java.util.stream.*;

class ShrinkersTests {

	@Example
	void shrinkTreeForSymmetricIntRangeFrom0() {

		Shrinker<Integer> shrinker = Shrinkers.range(-10, 10);
		ShrinkTree<Integer> shrinkTree = shrinker.shrink(0);

		Stream<ShrinkValue<Integer>> shrinkingStream = shrinkTree.stream();

		Assertions.assertThat(shrinkingStream).containsExactly(
			ShrinkValue.of(0, 0),
			ShrinkValue.of(5, 50),
			ShrinkValue.of(8, 20),
			ShrinkValue.of(9, 10),
			ShrinkValue.of(-5, 50),
			ShrinkValue.of(-8, 20),
			ShrinkValue.of(-9, 10)
		);
	}
}
