package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

@Label("Arbitraries.functions()")
class ArbitrariesFunctionsTests {

	@Example
	void function_creates_same_result_for_same_input(@ForAll Random random) {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 10);
		Arbitrary<Function<String, Integer>> functions = Arbitraries.functions(Function.class, integers);

		Function<String, Integer> function = functions.generator(10).next(random).value();

		Integer valueForHello = function.apply("hello");
		assertThat(valueForHello).isBetween(1, 10);
		assertThat(function.apply("hello")).isEqualTo(valueForHello);
	}

	@Example
	void supplier_always_returns_same_element(@ForAll Random random) {
		Arbitrary<Integer> integers = Arbitraries.integers().between(1, 10);
		Arbitrary<Supplier<Integer>> functions = Arbitraries.functions(Supplier.class, integers);

		Supplier<Integer> supplier = functions.generator(10).next(random).value();

		Integer value = supplier.get();
		assertThat(value).isBetween(1, 10);
		assertThat(supplier.get()).isEqualTo(value);
	}

	@Example
	void consumer_accepts_anything(@ForAll Random random) {
		Arbitrary<Consumer<Integer>> functions = Arbitraries.functions(Consumer.class, Arbitraries.nothing());

		Consumer<Integer> supplier = functions.generator(10).next(random).value();

		supplier.accept(0);
		supplier.accept(Integer.MAX_VALUE);
	}

	@Example
	void functional_interfaces_and_SAM_types_are_accepted() {
		Arbitrary<Integer> any = Arbitraries.constant(1);

		assertThat(Arbitraries.functions(Function.class, any)).isNotNull();
		assertThat(Arbitraries.functions(Supplier.class, any)).isNotNull();
		assertThat(Arbitraries.functions(Consumer.class, Arbitraries.nothing())).isNotNull();
		assertThat(Arbitraries.functions(Predicate.class, any)).isNotNull();
		assertThat(Arbitraries.functions(MyFunctionalInterface.class, any)).isNotNull();
		assertThat(Arbitraries.functions(MyInheritedFunctionalInterface.class, any)).isNotNull();
		assertThat(Arbitraries.functions(MySamType.class, any)).isNotNull();
	}

	@Example
	void non_functional_interfaces_are_not_accepted() {
		Arbitrary<Integer> any = Arbitraries.constant(1);

		assertThatThrownBy(
			() -> Arbitraries.functions(NotAFunctionalInterface.class, any))
			.isInstanceOf(JqwikException.class);
		assertThatThrownBy(
			() -> Arbitraries.functions(MyAbstractClass.class, any))
			.isInstanceOf(JqwikException.class);
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

	interface NotAFunctionalInterface<P1, P2, R> {
		R take1(P1 p1, P2 p2);

		R take2(P1 p1, P2 p2);
	}

	static abstract class MyAbstractClass<P1, P2, R> {
		abstract R take(P1 p1, P2 p2);
	}

}
