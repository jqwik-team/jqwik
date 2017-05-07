package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class IntegerShrinkingTests {

	@Example
	void shrinkFrom0ShrinksTo0Only() {
		Shrinker<Integer> shrinker = Shrinkers.range(-10, 10);
		Shrinkable<Integer> shrinkable = shrinker.shrink(0);

		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyAll();
		Optional<ShrinkResult<Integer>> shrinkResult = shrinkable.shrink(falsifier);
		assertThat(shrinkResult.get()).isEqualTo(ShrinkResult.of(ShrinkableValue.of(0, 0), null));
		assertThat(falsifier.visited()).containsExactly(0);
	}

	@Example
	void shrinkFromValueOutsideRangeReturnsNothing() {
		Shrinker<Integer> shrinker = Shrinkers.range(-10, 10);
		Shrinkable<Integer> shrinkable = shrinker.shrink(20);

		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyAll();
		assertThat(shrinkable.shrink(falsifier)).isNotPresent();
	}

	@Example
	void shrinkPositiveValueTowards0If0isInRange() {

		Shrinker<Integer> shrinker = Shrinkers.range(-10, 20);
		ShrinkableSequence<Integer> shrinkable = (ShrinkableSequence<Integer>) shrinker.shrink(5);

		assertThat(shrinkable.steps()).containsExactly(
			ShrinkableValue.of(5, 5),
			ShrinkableValue.of(3, 3),
			ShrinkableValue.of(2, 2),
			ShrinkableValue.of(1, 1),
			ShrinkableValue.of(0, 0)
		);

		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyAll();
		Optional<ShrinkResult<Integer>> shrinkResult = shrinkable.shrink(falsifier);
		assertThat(shrinkResult.get()).isEqualTo(ShrinkResult.of(ShrinkableValue.of(0, 0), null));
		assertThat(falsifier.visited()).containsExactly(5, 3, 2, 1, 0);
	}

	@Example
	void shrinkNegativeValueTowards0If0isInRange() {

		Shrinker<Integer> shrinker = Shrinkers.range(-10, 20);
		ShrinkableSequence<Integer> shrinkable = (ShrinkableSequence<Integer>) shrinker.shrink(-5);

		assertThat(shrinkable.steps()).containsExactly(
			ShrinkableValue.of(-5, 5),
			ShrinkableValue.of(-3, 3),
			ShrinkableValue.of(-2, 2),
			ShrinkableValue.of(-1, 1),
			ShrinkableValue.of(0, 0)
		);

		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyAll();
		Optional<ShrinkResult<Integer>> shrinkResult = shrinkable.shrink(falsifier);
		assertThat(shrinkResult.get()).isEqualTo(ShrinkResult.of(ShrinkableValue.of(0, 0), null));
		assertThat(falsifier.visited()).containsExactly(-5, -3, -2, -1, 0);
	}

	@Example
	void shrinkNegativeValueTowardMaxIf0IsOutsideRange() {

		Shrinker<Integer> shrinker = Shrinkers.range(-20, -5);
		ShrinkableSequence<Integer> shrinkable = (ShrinkableSequence<Integer>) shrinker.shrink(-10);

		assertThat(shrinkable.steps()).containsExactly(
			ShrinkableValue.of(-10, 5),
			ShrinkableValue.of(-8, 3),
			ShrinkableValue.of(-7, 2),
			ShrinkableValue.of(-6, 1),
			ShrinkableValue.of(-5, 0)
		);

		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyAll();
		Optional<ShrinkResult<Integer>> shrinkResult = shrinkable.shrink(falsifier);
		assertThat(shrinkResult.get()).isEqualTo(ShrinkResult.of(ShrinkableValue.of(-5, 0), null));
		assertThat(falsifier.visited()).containsExactly(-10, -8, -7, -6, -5);
	}

	@Example
	void shrinkPositiveValueTowardMinIf0IsOutsideRange() {

		Shrinker<Integer> shrinker = Shrinkers.range(5, 20);
		ShrinkableSequence<Integer> shrinkable = (ShrinkableSequence<Integer>) shrinker.shrink(10);

		assertThat(shrinkable.steps()).containsExactly(
			ShrinkableValue.of(10, 5),
			ShrinkableValue.of(8, 3),
			ShrinkableValue.of(7, 2),
			ShrinkableValue.of(6, 1),
			ShrinkableValue.of(5, 0)
		);

		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyAll();
		Optional<ShrinkResult<Integer>> shrinkResult = shrinkable.shrink(falsifier);
		assertThat(shrinkResult.get()).isEqualTo(ShrinkResult.of(ShrinkableValue.of(5, 0), null));
		assertThat(falsifier.visited()).containsExactly(10, 8, 7, 6, 5);
	}

}
