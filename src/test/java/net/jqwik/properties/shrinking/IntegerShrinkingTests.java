package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

class IntegerShrinkingTests {

	private List<Integer> visited = new ArrayList<>();

	@Example
	void shrinkFrom0ShrinksTo0Only() {
		Shrinker<Integer> shrinker = Shrinkers.range(-10, 10);
		Shrinkable<Integer> shrinkable = shrinker.shrink(0);

		Optional<ShrinkResult<Integer>> shrinkResult = shrinkable.shrink(falsifyAll());
		assertThat(shrinkResult.get()).isEqualTo(ShrinkResult.of(ShrinkableValue.of(0, 0), null));
		assertThat(visited).containsExactly(0);
	}

	private Predicate<Integer> falsifyAll() {
		return value -> {
				visited.add(value);
				return false;
			};
	}

	@Example
	void shrinkFromValueOutsideRangeReturnsNothing() {
		Shrinker<Integer> shrinker = Shrinkers.range(-10, 10);
		Shrinkable<Integer> shrinkable = shrinker.shrink(20);

		assertThat(shrinkable.shrink(falsifyAll())).isNotPresent();
	}

	@Example
	void shrinkPositiveValueTowards0If0isInRange() {

		Shrinker<Integer> shrinker = Shrinkers.range(-10, 20);
		ShrinkableSequence<Integer> shrinkable = (ShrinkableSequence<Integer>) shrinker.shrink(5);

		assertThat(shrinkable.steps()).containsExactly(
			ShrinkableValue.of(5, 5),
			ShrinkableValue.of(2, 2),
			ShrinkableValue.of(1, 1),
			ShrinkableValue.of(0, 0)
		);

		Optional<ShrinkResult<Integer>> shrinkResult = shrinkable.shrink(falsifyAll());
		assertThat(shrinkResult.get()).isEqualTo(ShrinkResult.of(ShrinkableValue.of(0, 0), null));
		assertThat(visited).containsExactly(5, 2, 1, 0);
	}

	@Example
	void shrinkNegativeValueTowards0If0isInRange() {

		Shrinker<Integer> shrinker = Shrinkers.range(-10, 20);
		ShrinkableSequence<Integer> shrinkable = (ShrinkableSequence<Integer>) shrinker.shrink(-5);

		assertThat(shrinkable.steps()).containsExactly(
			ShrinkableValue.of(-5, 5),
			ShrinkableValue.of(-2, 2),
			ShrinkableValue.of(-1, 1),
			ShrinkableValue.of(0, 0)
		);

		Optional<ShrinkResult<Integer>> shrinkResult = shrinkable.shrink(falsifyAll());
		assertThat(shrinkResult.get()).isEqualTo(ShrinkResult.of(ShrinkableValue.of(0, 0), null));
		assertThat(visited).containsExactly(-5, -2, -1, 0);
	}

	@Example
	void shrinkNegativeValueTowardMaxIf0IsOutsideRange() {

		Shrinker<Integer> shrinker = Shrinkers.range(-20, -5);
		ShrinkableSequence<Integer> shrinkable = (ShrinkableSequence<Integer>) shrinker.shrink(-10);

		assertThat(shrinkable.steps()).containsExactly(
			ShrinkableValue.of(-10, 5),
			ShrinkableValue.of(-7, 2),
			ShrinkableValue.of(-6, 1),
			ShrinkableValue.of(-5, 0)
		);

		Optional<ShrinkResult<Integer>> shrinkResult = shrinkable.shrink(falsifyAll());
		assertThat(shrinkResult.get()).isEqualTo(ShrinkResult.of(ShrinkableValue.of(-5, 0), null));
		assertThat(visited).containsExactly(-10, -7, -6, -5);
	}

	@Example
	void shrinkPositiveValueTowardMinIf0IsOutsideRange() {

		Shrinker<Integer> shrinker = Shrinkers.range(5, 20);
		ShrinkableSequence<Integer> shrinkable = (ShrinkableSequence<Integer>) shrinker.shrink(10);

		assertThat(shrinkable.steps()).containsExactly(
			ShrinkableValue.of(10, 5),
			ShrinkableValue.of(7, 2),
			ShrinkableValue.of(6, 1),
			ShrinkableValue.of(5, 0)
		);

		Optional<ShrinkResult<Integer>> shrinkResult = shrinkable.shrink(falsifyAll());
		assertThat(shrinkResult.get()).isEqualTo(ShrinkResult.of(ShrinkableValue.of(5, 0), null));
		assertThat(visited).containsExactly(10, 7, 6, 5);
	}

}
