package net.jqwik.engine.properties.shrinking;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

import static net.jqwik.api.ShrinkingTestHelper.*;
import static net.jqwik.api.Tuple.*;

class LazyArbitraryShrinkingTests {

	@Property(tries = 10)
	void oneStep(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			Arbitraries.lazy(Arbitraries::integers);
		Integer value = shrinkToMinimal(arbitrary, random);
		Assertions.assertThat(value).isEqualTo(0);
	}

	@Property(tries = 10)
	void severalStepsToList(@ForAll Random random) {
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
	void severalStepsToListReversedLazy(@ForAll Random random) {
		Arbitrary<List<Integer>> arbitrary = listOfIntegerReversedLazy();
		TestingFalsifier<List<Integer>> falsifier = integers -> integers.size() < 2;
		RandomGenerator<List<Integer>> generator = arbitrary.generator(10);
		Shrinkable<List<Integer>> falsifiedShrinkable =
			ArbitraryTestHelper.generateUntil(generator, random, value -> {
				TryExecutionResult result = falsifier.execute(value);
				return result.isFalsified();
			});
		List<Integer> shrunkValue = shrinkToMinimal(falsifiedShrinkable, falsifier, null);

		Assertions.assertThat(shrunkValue.size()).isLessThanOrEqualTo(falsifiedShrinkable.value().size());
		// TODO: Should be improved
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
	@Disabled
	class Calculator {

		@Property
		void test(@ForAll("expression") Object expression) {
			Assume.that(divSubterms(expression));
			evaluate(expression);
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
			Arbitrary<Object> lazyExpression = Arbitraries.lazy(this::expression);

			return Arbitraries.frequencyOf(
				// Make integers more probable to prevent stack overflow
				Tuple.of(3, Arbitraries.integers()),
				Tuple.of(1, Combinators.combine(lazyExpression, lazyExpression)
									   .as((e1, e2) -> of("+", e1, e2))),
				Tuple.of(1, Combinators.combine(lazyExpression, lazyExpression)
									   .as((e1, e2) -> of("/", e1, e2)))
			);
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
