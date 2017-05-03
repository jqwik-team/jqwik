package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class IntegerShrinkingTests {

	@Example
	void shrinkFrom0ReturnsNothing() {
		Shrinker<Integer> shrinker = Shrinkers.range(-10, 10);
		ShrinkTree<Integer> shrinkTree = shrinker.shrink(0);

		assertThat(shrinkTree.shrinkingRoutes()).hasSize(0);
	}

	@Example
	void shrinkFromValueOutsideRangeReturnsNothing() {
		Shrinker<Integer> shrinker = Shrinkers.range(-10, 10);
		ShrinkTree<Integer> shrinkTree = shrinker.shrink(20);

		assertThat(shrinkTree.shrinkingRoutes()).hasSize(0);
	}

	@Example
	void shrinkIntRangeWithMin0() {

		Shrinker<Integer> shrinker = Shrinkers.range(0, 20);
		ShrinkTree<Integer> shrinkTree = shrinker.shrink(5);

		List<List<ShrinkValue<Integer>>> routes = shrinkTree.shrinkingRoutes();
		assertThat(routes).hasSize(2);
		assertThat(routes.get(0)).containsExactly(
			ShrinkValue.of(2, 2),
			ShrinkValue.of(1, 1),
			ShrinkValue.of(0, 0)
		);
		assertThat(routes.get(1)).containsExactly(
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

		List<List<ShrinkValue<Integer>>> routes = shrinkTree.shrinkingRoutes();
		assertThat(routes).hasSize(2);
		assertThat(routes.get(0)).containsExactly(
			ShrinkValue.of(-2, 2),
			ShrinkValue.of(-1, 1),
			ShrinkValue.of(0, 0)
		);
		assertThat(routes.get(1)).containsExactly(
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

		List<List<ShrinkValue<Integer>>> routes = shrinkTree.shrinkingRoutes();
		assertThat(routes).hasSize(2);
		assertThat(routes.get(0)).containsExactly(
			ShrinkValue.of(-125, 25),
			ShrinkValue.of(-112, 12),
			ShrinkValue.of(-106, 6),
			ShrinkValue.of(-103, 3),
			ShrinkValue.of(-101, 1),
			ShrinkValue.of(-100, 0)
		);
		assertThat(routes.get(1)).containsExactly(
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

		List<List<ShrinkValue<Integer>>> routes = shrinkTree.shrinkingRoutes();
		assertThat(routes).hasSize(2);
		assertThat(routes.get(0)).containsExactly(
			ShrinkValue.of(175, 25),
			ShrinkValue.of(188, 12),
			ShrinkValue.of(194, 6),
			ShrinkValue.of(197, 3),
			ShrinkValue.of(199, 1),
			ShrinkValue.of(200, 0)
		);
		assertThat(routes.get(1)).containsExactly(
			ShrinkValue.of(125, 25),
			ShrinkValue.of(112, 12),
			ShrinkValue.of(106, 6),
			ShrinkValue.of(103, 3),
			ShrinkValue.of(101, 1),
			ShrinkValue.of(100, 0)
		);
	}

	@Example
	void unconstrainedMaxShrinksOnlyTowards0() {

		Shrinker<Integer> shrinker = Shrinkers.range(0, Integer.MAX_VALUE);
		ShrinkTree<Integer> positiveShrinkTree = shrinker.shrink(150);

		List<List<ShrinkValue<Integer>>> routes = positiveShrinkTree.shrinkingRoutes();
		assertThat(routes).hasSize(1);
		assertThat(routes.get(0)).containsExactly(
			ShrinkValue.of(75, 75),
			ShrinkValue.of(37, 37),
			ShrinkValue.of(18, 18),
			ShrinkValue.of(9, 9),
			ShrinkValue.of(4, 4),
			ShrinkValue.of(2, 2),
			ShrinkValue.of(1, 1),
			ShrinkValue.of(0, 0)
		);
	}

	@Example
	void unconstrainedMinShrinksOnlyTowards0() {

		Shrinker<Integer> shrinker = Shrinkers.range(Integer.MIN_VALUE, 0);
		ShrinkTree<Integer> positiveShrinkTree = shrinker.shrink(-150);

		List<List<ShrinkValue<Integer>>> routes = positiveShrinkTree.shrinkingRoutes();
		assertThat(routes).hasSize(1);
		assertThat(routes.get(0)).containsExactly(
			ShrinkValue.of(-75, 75),
			ShrinkValue.of(-37, 37),
			ShrinkValue.of(-18, 18),
			ShrinkValue.of(-9, 9),
			ShrinkValue.of(-4, 4),
			ShrinkValue.of(-2, 2),
			ShrinkValue.of(-1, 1),
			ShrinkValue.of(0, 0)
		);
	}




}
