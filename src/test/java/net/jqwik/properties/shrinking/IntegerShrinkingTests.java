package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class IntegerShrinkingTests {

	@Example
	void shrinkFrom0ReturnsNothing() {
		Shrinker<Integer> shrinker = Shrinkers.range(-10, 10);
		ShrinkableChoice<Integer> shrinkTree = (ShrinkableChoice<Integer>) shrinker.shrink(0);

		assertThat(shrinkTree.routes()).hasSize(0);
	}

	@Example
	void shrinkFromValueOutsideRangeReturnsNothing() {
		Shrinker<Integer> shrinker = Shrinkers.range(-10, 10);
		ShrinkableChoice<Integer> shrinkTree = (ShrinkableChoice<Integer>) shrinker.shrink(20);

		assertThat(shrinkTree.routes()).hasSize(0);
	}

	@Example
	void shrinkIntRangeWithMin0() {

		Shrinker<Integer> shrinker = Shrinkers.range(0, 20);
		ShrinkableChoice<Integer> shrinkTree = (ShrinkableChoice<Integer>) shrinker.shrink(5);

		List<List<Shrinkable<Integer>>> routes = shrinkTree.routes();
		assertThat(routes).hasSize(2);
		assertThat(routes.get(0)).containsExactly(
			ShrinkableValue.of(2, 2),
			ShrinkableValue.of(1, 1),
			ShrinkableValue.of(0, 0)
		);
		assertThat(routes.get(1)).containsExactly(
			ShrinkableValue.of(13, 7),
			ShrinkableValue.of(17, 3),
			ShrinkableValue.of(19, 1),
			ShrinkableValue.of(20, 0)
		);
	}

	@Example
	void shrinkAsymmetricIntRangeWithMax0() {

		Shrinker<Integer> shrinker = Shrinkers.range(-20, 0);
		ShrinkableChoice<Integer> shrinkTree = (ShrinkableChoice<Integer>) shrinker.shrink(-5);

		List<List<Shrinkable<Integer>>> routes = shrinkTree.routes();
		assertThat(routes).hasSize(2);
		assertThat(routes.get(0)).containsExactly(
			ShrinkableValue.of(-2, 2),
			ShrinkableValue.of(-1, 1),
			ShrinkableValue.of(0, 0)
		);
		assertThat(routes.get(1)).containsExactly(
			ShrinkableValue.of(-13, 7),
			ShrinkableValue.of(-17, 3),
			ShrinkableValue.of(-19, 1),
			ShrinkableValue.of(-20, 0)
		);
	}

	@Example
	void shrinkAsymmetricIntRangeBelow0() {

		Shrinker<Integer> shrinker = Shrinkers.range(-200, -100);
		ShrinkableChoice<Integer> shrinkTree = (ShrinkableChoice<Integer>) shrinker.shrink(-150);

		List<List<Shrinkable<Integer>>> routes = shrinkTree.routes();
		assertThat(routes).hasSize(2);
		assertThat(routes.get(0)).containsExactly(
			ShrinkableValue.of(-125, 25),
			ShrinkableValue.of(-112, 12),
			ShrinkableValue.of(-106, 6),
			ShrinkableValue.of(-103, 3),
			ShrinkableValue.of(-101, 1),
			ShrinkableValue.of(-100, 0)
		);
		assertThat(routes.get(1)).containsExactly(
			ShrinkableValue.of(-175, 25),
			ShrinkableValue.of(-188, 12),
			ShrinkableValue.of(-194, 6),
			ShrinkableValue.of(-197, 3),
			ShrinkableValue.of(-199, 1),
			ShrinkableValue.of(-200, 0)
		);
	}

	@Example
	void shrinkAsymmetricIntRangeAbove0() {

		Shrinker<Integer> shrinker = Shrinkers.range(100, 200);
		ShrinkableChoice<Integer> shrinkTree = (ShrinkableChoice<Integer>) shrinker.shrink(150);

		List<List<Shrinkable<Integer>>> routes = shrinkTree.routes();
		assertThat(routes).hasSize(2);
		assertThat(routes.get(0)).containsExactly(
			ShrinkableValue.of(175, 25),
			ShrinkableValue.of(188, 12),
			ShrinkableValue.of(194, 6),
			ShrinkableValue.of(197, 3),
			ShrinkableValue.of(199, 1),
			ShrinkableValue.of(200, 0)
		);
		assertThat(routes.get(1)).containsExactly(
			ShrinkableValue.of(125, 25),
			ShrinkableValue.of(112, 12),
			ShrinkableValue.of(106, 6),
			ShrinkableValue.of(103, 3),
			ShrinkableValue.of(101, 1),
			ShrinkableValue.of(100, 0)
		);
	}

	@Example
	void unconstrainedMaxShrinksOnlyTowards0() {

		Shrinker<Integer> shrinker = Shrinkers.range(0, Integer.MAX_VALUE);
		ShrinkableChoice<Integer> positiveShrinkTree = (ShrinkableChoice<Integer>) shrinker.shrink(150);

		List<List<Shrinkable<Integer>>> routes = positiveShrinkTree.routes();
		assertThat(routes).hasSize(1);
		assertThat(routes.get(0)).containsExactly(
			ShrinkableValue.of(75, 75),
			ShrinkableValue.of(37, 37),
			ShrinkableValue.of(18, 18),
			ShrinkableValue.of(9, 9),
			ShrinkableValue.of(4, 4),
			ShrinkableValue.of(2, 2),
			ShrinkableValue.of(1, 1),
			ShrinkableValue.of(0, 0)
		);
	}

	@Example
	void unconstrainedMinShrinksOnlyTowards0() {

		Shrinker<Integer> shrinker = Shrinkers.range(Integer.MIN_VALUE, 0);
		ShrinkableChoice<Integer> positiveShrinkTree = (ShrinkableChoice<Integer>) shrinker.shrink(-150);

		List<List<Shrinkable<Integer>>> routes = positiveShrinkTree.routes();
		assertThat(routes).hasSize(1);
		assertThat(routes.get(0)).containsExactly(
			ShrinkableValue.of(-75, 75),
			ShrinkableValue.of(-37, 37),
			ShrinkableValue.of(-18, 18),
			ShrinkableValue.of(-9, 9),
			ShrinkableValue.of(-4, 4),
			ShrinkableValue.of(-2, 2),
			ShrinkableValue.of(-1, 1),
			ShrinkableValue.of(0, 0)
		);
	}




}
