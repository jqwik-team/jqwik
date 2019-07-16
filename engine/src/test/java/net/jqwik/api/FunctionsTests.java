package net.jqwik.api;

import java.util.*;
import java.util.function.*;

import org.assertj.core.api.*;

import static org.assertj.core.api.Assertions.*;

class FunctionsTests {

	@Example
	void function_creates_same_result_for_same_input(@ForAll Random random) {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 10);
		Arbitrary<Function<String, Integer>> functions =
			Functions.function(Function.class).returns(integers);

		Function<String, Integer> function = functions.generator(10).next(random).value();

		Integer valueForHello = function.apply("hello");
		assertThat(valueForHello).isBetween(1, 10);
		assertThat(function.apply("hello")).isEqualTo(valueForHello);
	}

	@Example
	void some_functions_create_different_result_for_different_input() {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 10);
		Arbitrary<Function<String, Integer>> functions =
			Functions.function(Function.class).returns(integers);

		ArbitraryTestHelper.assertAtLeastOneGenerated(
			functions.generator(10),
			function -> !function.apply("value1").equals(function.apply("value2"))
		);
	}

	@Example
	void null_value_is_accepted_as_input() {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 10);
		Arbitrary<Function<String, Integer>> functions =
			Functions.function(Function.class).returns(integers);

		ArbitraryTestHelper.assertAllGenerated(
			functions.generator(10),
			function -> function.apply(null) != null
		);
	}

	@Example
	void supplier_always_returns_same_element(@ForAll Random random) {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 10);
		Arbitrary<Supplier<Integer>> functions =
			Functions.function(Supplier.class).returns(integers);

		Supplier<Integer> supplier = functions.generator(10).next(random).value();

		Integer value = supplier.get();
		assertThat(value).isBetween(1, 10);
		assertThat(supplier.get()).isEqualTo(value);
	}

	@Example
	void consumer_accepts_anything(@ForAll Random random) {
		Arbitrary<Consumer<Integer>> functions =
			Functions.function(Consumer.class).returns(Arbitraries.nothing());

		Consumer<Integer> supplier = functions.generator(10).next(random).value();

		supplier.accept(0);
		supplier.accept(Integer.MAX_VALUE);
	}

	@Example
	void functional_interfaces_and_SAM_types_are_accepted() {
		Arbitrary<Integer> any = Arbitraries.constant(1);

		assertThat(Functions.function(Function.class).returns(any)).isNotNull();
		assertThat(Functions.function(Supplier.class).returns(any)).isNotNull();
		assertThat(Functions.function(Consumer.class).returns(Arbitraries.nothing())).isNotNull();
		assertThat(Functions.function(Predicate.class).returns(any)).isNotNull();
		assertThat(Functions.function(MyFunctionalInterface.class).returns(any)).isNotNull();
		assertThat(Functions.function(MyInheritedFunctionalInterface.class).returns(any)).isNotNull();
		assertThat(Functions.function(MySamType.class).returns(any)).isNotNull();
		assertThat(Functions.function(IntTransformer.class).returns(any)).isNotNull();
	}

	@Example
	void non_functional_interfaces_are_not_accepted() {
		Arbitrary<Integer> any = Arbitraries.constant(1);

		assertThatThrownBy(
			() -> Functions.function(NotAFunctionalInterface.class).returns(any))
			.isInstanceOf(JqwikException.class);
		assertThatThrownBy(
			() -> Functions.function(MyAbstractClass.class).returns(any))
			.isInstanceOf(JqwikException.class);
	}

	@Property(tries = 100)
	void functions_are_shrunk_to_constant_functions(@ForAll Random random) {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 20);
		Arbitrary<Function<String, Integer>> functions =
			Functions.function(Function.class).returns(integers);

		Function<String, Integer> shrunkFunction =
			ArbitraryTestHelper.falsifyThenShrink(functions, random, f -> f.apply("value1") < 11);

		Assertions.assertThat(shrunkFunction.apply("value1")).isEqualTo(11);
		Assertions.assertThat(shrunkFunction.apply("value2")).isEqualTo(11);
		Assertions.assertThat(shrunkFunction.apply("any")).isEqualTo(11);
	}

	@Group
	class Conditional_results {
		@Example
		void function_with_conditional_answer() {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 100);
			Arbitrary<Function<String, Integer>> functions =
				Functions
					.function(Function.class).returns(integers)
					.when(params -> params.get(0).equals("three"), params -> 3)
					.when(params -> params.get(0).equals("four"), params -> 4);

			ArbitraryTestHelper.assertAllGenerated(
				functions.generator(10),
				function -> function.apply("three") == 3 && function.apply("four") == 4
			);
		}

		@Example
		void first_matching_conditional_answer_is_used() {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 100);
			Arbitrary<Function<String, Integer>> functions =
				Functions
					.function(Function.class).returns(integers)
					.when(params -> params.get(0).equals("three"), params -> 3)
					.when(params -> params.get(0).equals("three"), params -> 33);

			ArbitraryTestHelper.assertAllGenerated(
				functions.generator(10),
				function -> function.apply("three") == 3
			);
		}

		@Example
		void function_with_conditional_null_answer() {
			Arbitrary<String> integers = Arbitraries.of("1", "2", "3");
			Arbitrary<Function<String, String>> functions =
				Functions
					.function(Function.class).returns(integers)
					.when(params -> params.get(0).equals("null"), params -> null);

			ArbitraryTestHelper.assertAllGenerated(
				functions.generator(10),
				function -> function.apply("null") == null
			);
		}

		@Example
		void function_with_conditional_exception() {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 100);
			Arbitrary<Function<String, Integer>> functions =
				Functions
					.function(Function.class).returns(integers)
					.when(params -> params.get(0) == null, params -> {
						throw new IllegalArgumentException();
					});

			ArbitraryTestHelper.assertAllGenerated(
				functions.generator(10),
				function -> {
					assertThatThrownBy(
						() -> function.apply(null)
					).isInstanceOf(IllegalArgumentException.class);
				}
			);
		}

		@Example
		void conditional_answer_works_when_shrunk(@ForAll Random random) {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 100);
			Arbitrary<Function<String, Integer>> functions =
				Functions
					.function(Function.class).returns(integers)
					.when(params -> params.get(0).equals("three"), params -> 3);

			Function<String, Integer> shrunkFunction = ArbitraryTestHelper.shrinkToEnd(functions, random);

			assertThat(shrunkFunction.apply("three")).isEqualTo(3);
		}

	}

	interface MySamType<P1, P2, R> {
		R take(P1 p1, P2 p2);
	}

	interface MySupplier<R> {
		R take();
	}

	interface MyConsumer<P> {
		void take(P p);
	}

	@FunctionalInterface
	interface MyFunctionalInterface<P1, P2, R> {
		R take(P1 p1, P2 p2);
	}

	interface MyInheritedFunctionalInterface<P1, P2, R> extends MyFunctionalInterface {
	}

	interface IntTransformer {
		int transform(int anInt);
	}

	interface NotAFunctionalInterface<P1, P2, R> {
		R take1(P1 p1, P2 p2);

		R take2(P1 p1, P2 p2);
	}

	static abstract class MyAbstractClass<P1, P2, R> {
		abstract R take(P1 p1, P2 p2);
	}

}
