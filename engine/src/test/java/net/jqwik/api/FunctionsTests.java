package net.jqwik.api;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.edgeCases.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ArbitraryTestHelper.*;
import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

class FunctionsTests {

	@Property(tries = 100)
	void manyCallsToFunction(@ForAll("stringToIntegerFunctions") Function<String, Integer> function, @ForAll @AlphaChars String aString) {
		Integer valueForHello = function.apply(aString);
		assertThat(valueForHello).isBetween(-100, 100);
		assertThat(function.apply(aString)).isEqualTo(valueForHello);

		int valueForHelloPlus1 = function.andThen(i -> i + 1).apply(aString);
		assertThat(valueForHelloPlus1).isEqualTo(valueForHello + 1);
	}

	@Provide
	Arbitrary<Function<String, Integer>> stringToIntegerFunctions() {
		Arbitrary<Integer> integers = Arbitraries.integers().between(-100, 100);
		return Functions.function(Function.class).returning(integers);
	}

	@Example
	void function_creates_same_result_for_same_input(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 10);
		Arbitrary<Function<String, Integer>> functions =
			Functions.function(Function.class).returning(integers);

		Function<String, Integer> function = functions.generator(10, true).next(random).value();

		Integer valueForHello = function.apply("hello");
		assertThat(valueForHello).isBetween(1, 10);
		assertThat(function.apply("hello")).isEqualTo(valueForHello);
	}

	@Example
	void some_functions_create_different_result_for_different_input(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 10);
		Arbitrary<Function<String, Integer>> functions =
			Functions.function(Function.class).returning(integers);

		checkAtLeastOneGenerated(
			functions.generator(10, true),
			random,
			function -> !function.apply("value1").equals(function.apply("value2"))
		);
	}

	@Example
	void toString_of_functions_can_be_called(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integers = Arbitraries.just(42);
		Arbitrary<Function<String, Integer>> functions =
			Functions.function(Function.class).returning(integers);

		Function<String, Integer> function = functions.generator(10, true).next(random).value();
		assertThat(function.toString()).contains("Function");
	}

	@Example
	@StatisticsReport(onFailureOnly = true)
	void hashCode_of_functions_can_be_called(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 10000);
		Arbitrary<Function<String, Integer>> functions =
			Functions.function(Function.class).returning(integers);

		RandomGenerator<Function<String, Integer>> generator = functions.generator(10, true);
		Function<String, Integer> function1 = generator.next(random).value();

		assertThat(function1.hashCode()).isEqualTo(function1.hashCode());
		Function<String, Integer> function2 = generator.next(random).value();
		while (function2.apply("a").equals(function1.apply("a"))) {
			function2 = generator.next(random).value();
		}

		// In rare cases the hash code can be the same despite the functions producing different results
		boolean hashCodesAreDifferent = function1.hashCode() != function2.hashCode();
		Statistics.label("hash codes are different")
				  .collect(hashCodesAreDifferent)
				  .coverage(checker -> checker.check(true).percentage(p -> p > 95));
	}

	@Example
	void equals_of_functions_can_be_called(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 10000);
		Arbitrary<Function<String, Integer>> functions =
			Functions.function(Function.class).returning(integers);

		RandomGenerator<Function<String, Integer>> generator = functions.generator(10, true);
		Function<String, Integer> function1 = generator.next(random).value();

		assertThat(function1.equals(function1)).isTrue();
		assertThat(function1.equals(generator.next(random).value())).isFalse();
	}

	@Example
	void default_methods_of_functions_can_be_called(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integers = Arbitraries.just(42);
		Arbitrary<Function<String, Integer>> functions =
			Functions.function(Function.class).returning(integers);

		Function<String, Integer> function = functions.generator(10, true).next(random).value();

		Function<String, Integer> andThenFunction = function.andThen(value -> value - 1);
		assertThat(function.apply("any")).isEqualTo(42);
		assertThat(andThenFunction.apply("any")).isEqualTo(41);
	}

	@Example
	void default_methods_of_self_made_functional_interface_can_be_called(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integers = Arbitraries.just(42);
		Arbitrary<MyFunctionalInterface<String, String, Integer>> functions =
			Functions.function(MyFunctionalInterface.class).returning(integers);

		MyFunctionalInterface<String, String, Integer> function = functions.generator(10, true).next(random).value();
		assertThat(function.hello()).isEqualTo("hello");
	}

	@Example
	void null_value_is_accepted_as_input(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 10);
		Arbitrary<Function<String, Integer>> functions =
			Functions.function(Function.class).returning(integers);

		checkAllGenerated(
			functions.generator(10, true),
			random,
			function -> function.apply(null) != null
		);
	}

	@Example
	void supplier_always_returns_same_element(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 10);
		Arbitrary<Supplier<Integer>> functions =
			Functions.function(Supplier.class).returning(integers);

		Supplier<Integer> supplier = functions.generator(10, true).next(random).value();

		Integer value = supplier.get();
		assertThat(value).isBetween(1, 10);
		assertThat(supplier.get()).isEqualTo(value);
	}

	@Example
	void consumer_accepts_anything(@ForAll JqwikRandom random) {
		Arbitrary<Consumer<Integer>> functions =
			Functions.function(Consumer.class).returning(Arbitraries.nothing());

		Consumer<Integer> supplier = functions.generator(10, true).next(random).value();

		supplier.accept(0);
		supplier.accept(Integer.MAX_VALUE);
	}

	@Example
	void functional_interfaces_and_SAM_types_are_accepted() {
		Arbitrary<Integer> any = Arbitraries.just(1);

		assertThat(Functions.function(Function.class).returning(any)).isNotNull();
		assertThat(Functions.function(Supplier.class).returning(any)).isNotNull();
		assertThat(Functions.function(Consumer.class).returning(Arbitraries.nothing())).isNotNull();
		assertThat(Functions.function(Predicate.class).returning(any)).isNotNull();
		assertThat(Functions.function(MyFunctionalInterface.class).returning(any)).isNotNull();
		assertThat(Functions.function(MyInheritedFunctionalInterface.class).returning(any)).isNotNull();
		assertThat(Functions.function(MySamType.class).returning(any)).isNotNull();
		assertThat(Functions.function(IntTransformer.class).returning(any)).isNotNull();
	}

	@Example
	void non_functional_interfaces_are_not_accepted() {
		Arbitrary<Integer> any = Arbitraries.just(1);

		assertThatThrownBy(
			() -> Functions.function(NotAFunctionalInterface.class).returning(any))
			.isInstanceOf(JqwikException.class);
		assertThatThrownBy(
			() -> Functions.function(MyAbstractClass.class).returning(any))
			.isInstanceOf(JqwikException.class);
	}

	@Property(tries = 100, afterFailure = AfterFailureMode.RANDOM_SEED)
	void functions_are_shrunk_to_constant_functions(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 20);
		Arbitrary<Function<String, Integer>> functions =
			Functions.function(Function.class).returning(integers);

		Falsifier<Function<String, Integer>> falsifier =
			f -> (f.apply("value1") < 11) ?
					 TryExecutionResult.satisfied() :
					 TryExecutionResult.falsified(null);

		Function<String, Integer> shrunkFunction = falsifyThenShrink(functions, random, falsifier);
		assertThat(shrunkFunction.apply("value1")).isEqualTo(11);

		assertThat(shrunkFunction.apply("value2")).isEqualTo(11);
		assertThat(shrunkFunction.apply("any")).isEqualTo(11);
	}

	@Property(tries = 100)
	@ExpectFailure(checkResult = ShrinkToConstantFunction.class)
	void functions_are_shrunk_to_constant_functions(
		@ForAll Function<String, Integer> function,
		@ForAll @AlphaChars @StringLength(1) String aString
	) {
		int result = function.apply(aString);
		assertThat(result).isLessThan(11);
	}

	@SuppressWarnings("unchecked")
	private class ShrinkToConstantFunction implements Consumer<PropertyExecutionResult> {
		@Override
		public void accept(PropertyExecutionResult result) {
			Function<String, Integer> function = (Function<String, Integer>) result.falsifiedParameters().get().get(0);
			String string = (String) result.falsifiedParameters().get().get(1);
			assertThat(function.apply(string)).isEqualTo(11);
		}
	}


	@Group
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Integer> integers = Arbitraries.integers().between(10, 100);
			FunctionArbitrary<Object, Integer> functionArbitrary = Functions.function(Function.class).returning(integers);
			return Arbitraries.ofSuppliers(
				() -> functionArbitrary,
				() -> functionArbitrary.when(p -> p.get(0) == null, ignore -> 20)
			);
		}
	}


	@Group
	class EdgeCaseGeneration implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				functionArbitrary(),
				functionArbitrary().when(p -> p.get(0) == null, ignore -> 20)
			);
		}

		@Example
		void functionHasConstantFunctionsAsEdgeCases() {
			FunctionArbitrary<Function<String, Integer>, Integer> arbitrary = functionArbitrary();

			EdgeCases<Function<String, Integer>> edgeCases = arbitrary.edgeCases();
			Set<Function<String, Integer>> functions = collectEdgeCaseValues(edgeCases);
			assertThat(functions).hasSize(4);

			for (Function<String, Integer> function : functions) {
				assertThat(function.apply("any string")).isIn(10, 11, 99, 100);
			}

			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(4);
		}

		private FunctionArbitrary<Function<String, Integer>, Integer> functionArbitrary() {
			Arbitrary<Integer> integers = Arbitraries.integers().between(10, 100);
			return Functions.function(Function.class).returning(integers);
		}
	}


	@Group
	class Conditional_results {
		@Example
		void function_with_conditional_answer(@ForAll JqwikRandom random) {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 100);
			Arbitrary<Function<String, Integer>> functions =
				Functions
					.function(Function.class).returning(integers)
					.when(params -> params.get(0).equals("three"), params -> 3)
					.when(params -> params.get(0).equals("four"), params -> 4);

			checkAllGenerated(
				functions.generator(10, true),
				random,
				function -> function.apply("three") == 3 && function.apply("four") == 4
			);
		}

		@Example
		void first_matching_conditional_answer_is_used(@ForAll JqwikRandom random) {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 100);
			Arbitrary<Function<String, Integer>> functions =
				Functions
					.function(Function.class).returning(integers)
					.when(params -> params.get(0).equals("three"), params -> 3)
					.when(params -> params.get(0).equals("three"), params -> 33);

			checkAllGenerated(
				functions.generator(10, true),
				random,
				function -> function.apply("three") == 3
			);
		}

		@Example
		void function_with_conditional_null_answer(@ForAll JqwikRandom random) {
			Arbitrary<String> integers = Arbitraries.of("1", "2", "3");
			Arbitrary<Function<String, String>> functions =
				Functions
					.function(Function.class).returning(integers)
					.when(params -> params.get(0).equals("null"), params -> null);

			checkAllGenerated(
				functions.generator(10, true),
				random,
				function -> function.apply("null") == null
			);
		}

		@Example
		void function_with_conditional_exception() {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 100);
			Arbitrary<Function<String, Integer>> functions =
				Functions
					.function(Function.class).returning(integers)
					.when(params -> params.get(0) == null, params -> {
						throw new IllegalArgumentException();
					});

			assertAllGenerated(
				functions.generator(10, true),
				function -> {
					assertThatThrownBy(
						() -> function.apply(null)
					).isInstanceOf(IllegalArgumentException.class);
				}
			);
		}

		@Example
		void conditional_answer_works_when_shrunk(@ForAll JqwikRandom random) {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 100);
			Arbitrary<Function<String, Integer>> functions =
				Functions
					.function(Function.class).returning(integers)
					.when(params -> params.get(0).equals("three"), params -> 3);

			Function<String, Integer> shrunkFunction = ShrinkingSupport.falsifyThenShrink(functions, random);

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
	public interface MyFunctionalInterface<P1, P2, R> {
		R take(P1 p1, P2 p2);

		// Default method invocation only works for public interfaces
		default String hello() {
			return "hello";
		}
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
