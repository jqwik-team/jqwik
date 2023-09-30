package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingFalsifier.*;

@Group
@Label("CombinedShrinkable")
class CombinedShrinkableTests {

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Example
	void creation() {
		Shrinkable three = new OneStepShrinkable(3);
		Shrinkable hello = Shrinkable.unshrinkable("hello");
		Function<List<Object>, String> combinator = shrinkables -> {
			int anInt = (int) shrinkables.get(0);
			String aString = (String) shrinkables.get(1);
			return aString + anInt;
		};

		List<Shrinkable<Object>> shrinkables = Arrays.asList(three, hello);
		Shrinkable<String> shrinkable = new CombinedShrinkable<>(shrinkables, combinator);

		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(3, 0));
		assertThat(shrinkable.value()).isEqualTo("hello3");
	}

	@Example
	void creationWithEmptyShrinkablesList() {
		Function<List<Object>, String> combinator = shrinkables -> {
			return "constant";
		};

		List<Shrinkable<Object>> shrinkables = Arrays.asList();
		Shrinkable<String> shrinkable = new CombinedShrinkable<>(shrinkables, combinator);

		assertThat(shrinkable.value()).isEqualTo("constant");
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(0));
	}

	@Group
	class Shrinking {

		@SuppressWarnings({"rawtypes", "unchecked"})
		@Example
		void shrinkingToBottom() {
			Shrinkable three = new OneStepShrinkable(3);
			Shrinkable five = new OneStepShrinkable(5);
			Function<List<Object>, Tuple2<Integer, Integer>> combinator = shrinkables -> {
				int first = (int) shrinkables.get(0);
				int second = (int) shrinkables.get(1);
				return Tuple.of(first, second);
			};

			List<Shrinkable<Object>> shrinkables = Arrays.asList(three, five);
			Shrinkable<Tuple2<Integer, Integer>> shrinkable = new CombinedShrinkable<>(shrinkables, combinator);

			Tuple2<Integer, Integer> shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(Tuple.of(0, 0));
		}

		@SuppressWarnings({"rawtypes", "unchecked"})
		@Example
		void shrinkToCondition() {
			Shrinkable three = new OneStepShrinkable(3);
			Shrinkable five = new OneStepShrinkable(5);
			Function<List<Object>, Tuple2<Integer, Integer>> combinator = shrinkables -> {
				int first = (int) shrinkables.get(0);
				int second = (int) shrinkables.get(1);
				return Tuple.of(first, second);
			};

			List<Shrinkable<Object>> shrinkables = Arrays.asList(three, five);
			Shrinkable<Tuple2<Integer, Integer>> shrinkable = new CombinedShrinkable<>(shrinkables, combinator);

			Tuple2<Integer, Integer> shrunkValue = shrink(shrinkable, falsifier(tuple -> tuple.get1() + tuple.get2() < 4), null);
			assertThat(shrunkValue.get1() + shrunkValue.get2()).isEqualTo(4);
		}

		@SuppressWarnings({"rawtypes", "unchecked"})
		@Example
		void shrinkingWithFilter() {
			Shrinkable three = new OneStepShrinkable(3);
			Shrinkable five = new OneStepShrinkable(5);
			Function<List<Object>, Tuple2<Integer, Integer>> combinator = shrinkables -> {
				int first = (int) shrinkables.get(0);
				int second = (int) shrinkables.get(1);
				return Tuple.of(first, second);
			};

			List<Shrinkable<Object>> shrinkables = Arrays.asList(three, five);
			Shrinkable<Tuple2<Integer, Integer>> shrinkable = new CombinedShrinkable<>(shrinkables, combinator);

			Falsifier<Tuple2<Integer, Integer>> falsifier = tuple2 -> {
				int sum = tuple2.get1() + tuple2.get2();
				if (sum % 2 == 0) {
					return TryExecutionResult.invalid();
				}
				return TryExecutionResult.falsified(null);
			};
			Tuple2<Integer, Integer> shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(Tuple.of(0, 1));
		}

	}

}
