package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.*;

import static net.jqwik.api.ShrinkingTestHelper.*;
import static net.jqwik.api.Tuple.*;

@PropertyDefaults(tries = 10, afterFailure = AfterFailureMode.RANDOM_SEED)
class LazyOfArbitraryShrinkingTests {

	@Property(seed = "42", tries = 1)
	void shrinkToOtherSuppliers(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			Arbitraries.lazyOf(
				() -> Arbitraries.integers().between(1, 10),
				() -> Arbitraries.integers().between(1, 20).filter(i -> i > 10),
				() -> Arbitraries.integers().between(1, 30).filter(i -> i > 20),
				() -> Arbitraries.integers().between(1, 40).filter(i -> i > 30)
			);
		Integer value = shrinkToMinimal(arbitrary, random);
		Assertions.assertThat(value).isEqualTo(1);
	}

	@Property
	void oneStep(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			Arbitraries.lazyOf(Arbitraries::integers);
		Integer value = shrinkToMinimal(arbitrary, random);
		Assertions.assertThat(value).isEqualTo(0);
	}

	@Property
	void severalStepsToList(@ForAll Random random) {
		Arbitrary<List<Integer>> arbitrary = listOfInteger();
		TestingFalsifier<List<Integer>> falsifier = integers -> integers.size() < 2;
		List<Integer> shrunkValue = falsifyThenShrink(arbitrary, random, falsifier);

		Assertions.assertThat(shrunkValue).isEqualTo(Arrays.asList(1, 1));
	}

	@Provide
	Arbitrary<List<Integer>> listOfInteger() {
		return Arbitraries.lazyOf(
			() -> Arbitraries.integers().between(1, 5).list().ofSize(1),
			() -> Combinators.combine(listOfInteger(), listOfInteger()).as((l1, l2) -> {
				ArrayList<Integer> newList = new ArrayList<>(l1);
				newList.addAll(l2);
				return newList;
			})
		);
	}

	@Property
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
		Assertions.assertThat(shrunkValue).isEqualTo(Arrays.asList(1, 1));
	}

	@Provide
	Arbitrary<List<Integer>> listOfIntegerReversedLazy() {
		return Arbitraries.lazyOf(
			() -> Combinators.combine(listOfIntegerReversedLazy(), listOfIntegerReversedLazy()).as((l1, l2) -> {
				ArrayList<Integer> newList = new ArrayList<>(l1);
				newList.addAll(l2);
				return newList;
			}),
			() -> Arbitraries.integers().between(1, 5).list().ofSize(1)
		);
	}

	@Group
	class Calculator {

		/**
		 * Shrinking can take very long here
		 * - @Property(seed="1393175782937919151"): a few seconds
		 * - @Property(seed="-8068746813971981237"): I didn't bother to wait
		 *
		 * Moreover shrinking results are usually small (5 - 10 nodes) but
		 * are sometimes very large (200 nodes and more)
		 */
		//@Property(tries = 1000)//(seed="3404249936767611181") // This seed produces the desired result
		@ExpectFailure(checkResult = ShrinkToSmallExpression.class)
		// @Report(Reporting.GENERATED)
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
			Tuple3 tupleExpression = (Tuple3) expression;
			if (tupleExpression.get1().equals("/") && tupleExpression.get3().equals(0)) {
				return false;
			}
			return divSubterms(tupleExpression.get2()) && divSubterms(tupleExpression.get3());
		}

		@Provide
		Arbitrary<Object> expression() {
			return Arbitraries.lazyOf(
				() -> Arbitraries.integers(),
				() -> Arbitraries.integers(),
				() -> Arbitraries.integers(),
				() -> Combinators.combine(getLazy(), getLazy())
									   .as((e1, e2) -> of("+", e1, e2)),
				() -> Combinators.combine(getLazy(), getLazy())
									   .as((e1, e2) -> of("/", e1, e2))
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
