package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.testing.*;

import static net.jqwik.api.Tuple.*;
import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

class LazyArbitraryShrinkingTests {

	@Property(tries = 10)
	void oneStep(@ForAll JqwikRandom random) {
		Arbitrary<Integer> arbitrary =
			Arbitraries.lazy(Arbitraries::integers);
		Integer value = falsifyThenShrink(arbitrary, random);
		Assertions.assertThat(value).isEqualTo(0);
	}

	// Fixed seed because in rare cases it can take VERY long
	@Property(tries = 10, seed = "42")
	void severalStepsToList(@ForAll JqwikRandom random) {
		Arbitrary<List<Integer>> arbitrary = listOfInteger();
		TestingFalsifier<List<Integer>> falsifier = integers -> integers.size() < 2;
		List<Integer> shrunkValue = falsifyThenShrink(arbitrary, random, falsifier);

		Assertions.assertThat(shrunkValue).isEqualTo(Arrays.asList(1, 1));
	}

	@Provide
	Arbitrary<List<Integer>> listOfInteger() {
		Arbitrary<List<Integer>> lazyList = Arbitraries.lazy(this::listOfInteger);

		return Arbitraries.oneOf(
			Arbitraries.of(1, 2, 3, 4, 5).list().ofSize(1),
			Combinators.combine(lazyList, lazyList).as((l1, l2) -> {
				ArrayList<Integer> newList = new ArrayList<>(l1);
				newList.addAll(l2);
				return newList;
			})
		);
	}

	@Property(tries = 10, afterFailure = AfterFailureMode.RANDOM_SEED)
	void severalStepsToListReversedLazy(@ForAll JqwikRandom random) {
		Arbitrary<List<Integer>> arbitrary = listOfIntegerReversedLazy();
		TestingFalsifier<List<Integer>> falsifier = integers -> integers.size() < 2;
		RandomGenerator<List<Integer>> generator = arbitrary.generator(10, true);
		Shrinkable<List<Integer>> falsifiedShrinkable =
			generateUntil(generator, random, value -> {
				TryExecutionResult result = falsifier.execute(value);
				return result.isFalsified();
			});
		List<Integer> shrunkValue = shrink(falsifiedShrinkable, falsifier, null);

		Assertions.assertThat(shrunkValue.size()).isLessThanOrEqualTo(falsifiedShrinkable.value().size());
		// TODO: Shrinking should be improved so that:
		// Assertions.assertThat(shrunkValue).isEqualTo(Arrays.asList(1, 1));
	}

	@Provide
	Arbitrary<List<Integer>> listOfIntegerReversedLazy() {
		Arbitrary<List<Integer>> lazyList = Arbitraries.lazy(this::listOfIntegerReversedLazy);

		return Arbitraries.oneOf(
			Combinators.combine(lazyList, lazyList).as((l1, l2) -> {
				ArrayList<Integer> newList = new ArrayList<>(l1);
				newList.addAll(l2);
				return newList;
			}),
			Arbitraries.of(1, 2, 3, 4, 5).list().ofSize(1)
		);
	}

	@Group
	class Calculator {

		/**
		 * Shrinking can take very long here depending on initially found falsifying sample
		 *
		 * Moreover shrinking results are usually small (5 - 10 nodes) but
		 * are sometimes very large (200 nodes and more)
		 *
		 * @see LazyOfArbitraryShrinkingTests.Calculator
		 */
		@Property(seed="340424993676761") // This seed produces the desired result
		@ExpectFailure(checkResult = ShrinkToSmallExpression.class)
		void shrinkExpressionTree(@ForAll("expression") Object expression) {
			Assume.that(divSubterms(expression));
			evaluate(expression);
		}

		private class ShrinkToSmallExpression implements Consumer<PropertyExecutionResult> {
			@Override
			public void accept(PropertyExecutionResult propertyExecutionResult) {
				List<Object> actual = propertyExecutionResult.falsifiedParameters().get();
				// The best shrinker should shrink to just 5 nodes
				Assertions.assertThat(countNodes(actual.get(0))).isLessThanOrEqualTo(5);
			}

			private int countNodes(Object expression) {
				if (expression instanceof Integer) {
					return 1;
				};
				@SuppressWarnings("rawtypes")
				Tuple3 tupleExpression = (Tuple3) expression;
				return 1 + countNodes(tupleExpression.get2()) + countNodes(tupleExpression.get3());
			}
		}

		private boolean divSubterms(final Object expression) {
			if (expression instanceof Integer) {
				return true;
			}
			@SuppressWarnings("rawtypes")
			Tuple.Tuple3 tupleExpression = (Tuple.Tuple3) expression;
			if (tupleExpression.get1().equals("/") && tupleExpression.get3().equals(0)) {
				return false;
			}
			return divSubterms(tupleExpression.get2()) && divSubterms(tupleExpression.get3());
		}

		@Provide
		Arbitrary<Object> expression() {
			return Arbitraries.frequencyOf(
				// Make integers more probable to prevent stack overflow
				Tuple.of(3, Arbitraries.integers()),
				Tuple.of(1, Combinators.combine(getLazy(), getLazy())
									   .as((e1, e2) -> of("+", e1, e2))),
				Tuple.of(1, Combinators.combine(getLazy(), getLazy())
									   .as((e1, e2) -> of("/", e1, e2)))
			);
		}

		private Arbitrary<Object> getLazy() {
			return Arbitraries.lazy(this::expression);
		}

		int evaluate(Object expression) {
			if (expression instanceof Integer) {
				return (int) expression;
			}
			@SuppressWarnings("rawtypes")
			Tuple3 tupleExpression = (Tuple3) expression;
			if (tupleExpression.get1().equals("+")) {
				return evaluate(tupleExpression.get2()) + evaluate(tupleExpression.get3());
			}
			if (tupleExpression.get1().equals("/")) {
				return evaluate(tupleExpression.get2()) / evaluate(tupleExpression.get3());
			}
			throw new IllegalArgumentException(String.format("%s is not a valid expression", expression));
		}

	}

}
