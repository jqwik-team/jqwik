package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;
import net.jqwik.testing.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingFalsifier.*;

@Group
@Label("CollectShrinkable")
class CollectShrinkableTests {

	@Example
	void creation() {
		Shrinkable<Integer> shrinkable3 = new OneStepShrinkable(3);
		Shrinkable<Integer> shrinkable2 = new OneStepShrinkable(2);
		Shrinkable<Integer> shrinkable1 = new OneStepShrinkable(1);

		List<Shrinkable<Integer>> shrinkables = asList(shrinkable3, shrinkable2, shrinkable1);

		Predicate<List<?>> untilIsIgnoredHere = l -> true;
		CollectShrinkable<Integer> shrinkable = new CollectShrinkable<>(shrinkables, untilIsIgnoredHere);

		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(3, 6));
		assertThat(shrinkable.value()).containsExactly(3, 2, 1);
	}

	@Group
	class Shrinking {

		@Example
		void shrinkingWithoutSizeReduction() {
			Shrinkable<Integer> shrinkable4 = new OneStepShrinkable(3);
			Shrinkable<Integer> shrinkable1 = new OneStepShrinkable(1);

			List<Shrinkable<Integer>> shrinkables = asList(shrinkable4, shrinkable1);

			Predicate<List<?>> untilSizeAtLeast2 = l -> l.size() >= 2;
			Shrinkable<List<Integer>> shrinkable = new CollectShrinkable<>(shrinkables, untilSizeAtLeast2);

			List<Integer> shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).containsExactly(0, 0);
		}

		@Example
		void shrinkingWithFilter() {
			Shrinkable<Integer> shrinkable4 = new OneStepShrinkable(13);
			Shrinkable<Integer> shrinkable1 = new OneStepShrinkable(11);

			List<Shrinkable<Integer>> shrinkables = asList(shrinkable4, shrinkable1);

			Predicate<List<?>> untilSizeAtLeast2 = l -> l.size() >= 2;
			Shrinkable<List<Integer>> shrinkable = new CollectShrinkable<>(shrinkables, untilSizeAtLeast2);

			Falsifier<List<Integer>> falsifier = collection -> {
				int sum = collection.stream().mapToInt(i -> i).sum();
				if (sum % 2 == 1) {
					return TryExecutionResult.invalid();
				}
				return TryExecutionResult.falsified(null);
			};
			List<Integer> shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).containsExactly(0, 0);
		}

		@Example
		void shrinkingWithoutSizeReductionButNotAllShrinkingStepsWork() {
			Shrinkable<Integer> shrinkable4 = new FullShrinkable(5);
			Shrinkable<Integer> shrinkable1 = new FullShrinkable(1);

			List<Shrinkable<Integer>> shrinkables = asList(shrinkable4, shrinkable1);

			Predicate<List<?>> untilSizeAtLeast2 = l -> l.size() >= 2;
			Shrinkable<List<Integer>> shrinkable = new CollectShrinkable<>(shrinkables, untilSizeAtLeast2);

			TestingFalsifier<List<Integer>> sumMustNotBeEven = listOfInts -> {
				int sum = listOfInts.stream().mapToInt(i -> i).sum();
				return sum % 2 != 0;
			};
			List<Integer> shrunkValue = shrink(shrinkable, sumMustNotBeEven, null);
			assertThat(shrunkValue).containsExactly(1, 1);
		}

		@Example
		void shrinkingWithSizeReduction() {
			Shrinkable<Integer> shrinkable1 = new OneStepShrinkable(2).map(i -> 3 - i);

			List<Shrinkable<Integer>> shrinkables =
				asList(shrinkable1, shrinkable1, shrinkable1, shrinkable1, shrinkable1, shrinkable1);

			Predicate<List<? extends Integer>> sumAtLeast6 = l -> {
				int sum = l.stream().mapToInt(i -> i).sum();
				return sum >= 6;
			};
			Shrinkable<List<Integer>> shrinkable = new CollectShrinkable<>(shrinkables, sumAtLeast6);
			List<Integer> shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).containsExactly(3, 3);
		}

		@Example
		void shrinkToFullySorted() {
			Shrinkable<Integer> shrinkable3 = new OneStepShrinkable(3);
			Shrinkable<Integer> shrinkable4 = new OneStepShrinkable(4);
			Shrinkable<Integer> shrinkable2 = new OneStepShrinkable(2);
			Shrinkable<Integer> shrinkable1 = new OneStepShrinkable(1);

			List<Shrinkable<Integer>> shrinkables = asList(shrinkable3, shrinkable4, shrinkable2, shrinkable1);

			Predicate<List<? extends Integer>> sumAtLeast10 = l -> {
				int sum = l.stream().mapToInt(i -> i).sum();
				return sum >= 10;
			};
			Shrinkable<List<Integer>> shrinkable = new CollectShrinkable<>(shrinkables, sumAtLeast10);
			List<Integer> shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(asList(1, 2, 3, 4));
		}

		@Example
		void shrinkToPartiallySorted() {
			Shrinkable<Integer> shrinkable3 = new OneStepShrinkable(3);
			Shrinkable<Integer> shrinkable4 = new OneStepShrinkable(4);
			Shrinkable<Integer> shrinkable2 = new OneStepShrinkable(2);
			Shrinkable<Integer> shrinkable1 = new OneStepShrinkable(1);

			List<Shrinkable<Integer>> shrinkables = asList(shrinkable3, shrinkable4, shrinkable2, shrinkable1);

			Predicate<List<? extends Integer>> sumAtLeast10 = l -> {
				int sum = l.stream().mapToInt(i -> i).sum();
				return sum >= 10 && l.get(0) != 1;
			};
			Shrinkable<List<Integer>> shrinkable = new CollectShrinkable<>(shrinkables, sumAtLeast10);
			List<Integer> shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(asList(2, 1, 3, 4));
		}
	}

}
