package net.jqwik.properties.shrinking;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import java.util.stream.*;

class IntegerShrinkingTests {

	@Example
	void shrinkFrom0OnlyReturns0() {
		Shrinker<Integer> shrinker = Shrinkers.range(-10, 10);
		ShrinkTree<Integer> shrinkTree = shrinker.shrink(0);

		Stream<ShrinkValue<Integer>> shrinkingStream = shrinkTree.stream();

		Assertions.assertThat(shrinkingStream).containsExactly(
			ShrinkValue.of(0, 0)
		);
	}

	@Example
	void shrinkIntRangeWithMin0() {

		Shrinker<Integer> shrinker = Shrinkers.range(0, 20);
		ShrinkTree<Integer> shrinkTree = shrinker.shrink(5);

		Stream<ShrinkValue<Integer>> shrinkingStream = shrinkTree.stream();

		Assertions.assertThat(shrinkingStream).containsExactly(
			ShrinkValue.of(2, 2),
			ShrinkValue.of(1, 1),
			ShrinkValue.of(0, 0),
			ShrinkValue.of(13, 7),
			ShrinkValue.of(17, 3),
			ShrinkValue.of(19, 1),
			ShrinkValue.of(20, 0)
		);
	}

	@Example
	void shrinkAsymmetricIntRangeWithMax0() {

		Shrinker<Integer> shrinker = Shrinkers.range(-20, 0);
		ShrinkTree<Integer> shrinkTree = shrinker.shrink(-5);

		Stream<ShrinkValue<Integer>> shrinkingStream = shrinkTree.stream();

		Assertions.assertThat(shrinkingStream).containsExactly(
			ShrinkValue.of(-2, 2),
			ShrinkValue.of(-1, 1),
			ShrinkValue.of(0, 0),
			ShrinkValue.of(-13, 7),
			ShrinkValue.of(-17, 3),
			ShrinkValue.of(-19, 1),
			ShrinkValue.of(-20, 0)
		);
	}

	@Example
	void shrinkAsymmetricIntRangeBelow0() {

		Shrinker<Integer> shrinker = Shrinkers.range(-200, -100);
		ShrinkTree<Integer> shrinkTree = shrinker.shrink(-150);

		Stream<ShrinkValue<Integer>> shrinkingStream = shrinkTree.stream();

		Assertions.assertThat(shrinkingStream).containsExactly(
			ShrinkValue.of(-125, 25),
			ShrinkValue.of(-112, 12),
			ShrinkValue.of(-106, 6),
			ShrinkValue.of(-103, 3),
			ShrinkValue.of(-101, 1),
			ShrinkValue.of(-100, 0),
			ShrinkValue.of(-175, 25),
			ShrinkValue.of(-188, 12),
			ShrinkValue.of(-194, 6),
			ShrinkValue.of(-197, 3),
			ShrinkValue.of(-199, 1),
			ShrinkValue.of(-200, 0)
		);
	}

	@Example
	void shrinkAsymmetricIntRangeAbove0() {

		Shrinker<Integer> shrinker = Shrinkers.range(100, 200);
		ShrinkTree<Integer> shrinkTree = shrinker.shrink(150);

		Stream<ShrinkValue<Integer>> shrinkingStream = shrinkTree.stream();

		Assertions.assertThat(shrinkingStream).containsExactly(
			ShrinkValue.of(175, 25),
			ShrinkValue.of(188, 12),
			ShrinkValue.of(194, 6),
			ShrinkValue.of(197, 3),
			ShrinkValue.of(199, 1),
			ShrinkValue.of(200, 0),
			ShrinkValue.of(125, 25),
			ShrinkValue.of(112, 12),
			ShrinkValue.of(106, 6),
			ShrinkValue.of(103, 3),
			ShrinkValue.of(101, 1),
			ShrinkValue.of(100, 0)
		);
	}


	@Example
	void shrinkFromValueOutsideRangeOnlyReturnsNoValue() {
		Shrinker<Integer> shrinker = Shrinkers.range(-10, 10);
		ShrinkTree<Integer> shrinkTree = shrinker.shrink(20);

		Stream<ShrinkValue<Integer>> shrinkingStream = shrinkTree.stream();

		Assertions.assertThat(shrinkingStream).isEmpty();
	}

}
