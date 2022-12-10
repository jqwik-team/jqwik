package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingFalsifier.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
@Label("FilteredShrinkable")
class FilteredShrinkableTests {

	@Example
	void creation() {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 2 == 1);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(3));
		assertThat(shrinkable.value()).isEqualTo(3);
	}

	@Group
	class Shrinking {

		@Example
		void noStepShrinking() {
			Shrinkable<Integer> integerShrinkable = new FullShrinkable(3);
			Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 2 == 1);

			Integer shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(1);
		}

		@Example
		void singleStepShrinking() {
			Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
			Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 2 == 1);

			Integer shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(1);
		}

		@Example
		void manyStepsShrinking() {
			Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(49);
			Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i > 0 && i % 7 == 0);

			Integer shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(7);
		}

		@Property(tries = 10)
		void filteredIntegers(@ForAll JqwikRandom random) {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 40).filter(i -> i > 30);
			Shrinkable<Integer> shrinkable = generateUntil(integers.generator(10, true), random, i -> true);

			Integer shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(31);
		}

	}

	@Group
	class Growing {
		@Example
		void noStepGrowing() {
			Shrinkable<Integer> integerShrinkable = new FullShrinkable(3, 10);
			Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 2 == 1);

			Stream<Integer> grownValues = shrinkable.grow().map(Shrinkable::value);
			assertThat(grownValues).containsExactly(5, 7, 9);
		}

		@Example
		void multiStepGrowing() {
			Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3, 0, 15);
			Shrinkable<Integer> shrinkable = integerShrinkable.filter(i -> i % 3 == 0);

			Stream<Integer> grownValues = shrinkable.grow().map(Shrinkable::value);
			assertThat(grownValues).containsExactly(6, 9, 12, 15);
		}

	}

}
