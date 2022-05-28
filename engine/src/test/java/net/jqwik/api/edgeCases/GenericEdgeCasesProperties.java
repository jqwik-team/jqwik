package net.jqwik.api.edgeCases;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.engine.*;
import net.jqwik.engine.support.*;

import java.util.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

public interface GenericEdgeCasesProperties {

	@Provide
	Arbitrary<Arbitrary<?>> arbitraries();

	@Property
	default void askingForZeroEdgeCases(@ForAll("arbitraries") Arbitrary<?> arbitrary) {
		assertThat(arbitrary.edgeCases(0)).hasSize(0);
	}

	@Property
	default void askingForNegativeNumberOfEdgeCases(@ForAll("arbitraries") Arbitrary<?> arbitrary) {
		assertThat(arbitrary.edgeCases(-42)).hasSize(0);
	}

	@Property
	default void consistentItemOrder(
		@ForAll("arbitraries") Arbitrary<?> arbitrary,
		@ForAll("arbitraryTransformations") Function<Arbitrary<?>, Arbitrary<?>> transformation,
		@ForAll Random random,
		@ForAll @IntRange(min = 1, max = 20) int size
	) {
		Arbitrary<?> values = transformation.apply(arbitrary);
		RandomGenerator<?> gen = values.generator(1000, true);
		for (int i = 0; i < size; i++) {
			long seed = random.nextLong();
			// Both values should be equal with respect to "displayString"
			// For instance, element order in the sets should match
			Object valueA = gen.next(SourceOfRandomness.newRandom(seed)).value();
			Object valueB = gen.next(SourceOfRandomness.newRandom(seed)).value();
			assertThat(JqwikStringSupport.displayString(valueA))
				.isEqualTo(JqwikStringSupport.displayString(valueB));
		}
	}

	interface ArbitraryTransformer<U, V> extends Function<Arbitrary<U>, Arbitrary<V>> {
		static <E, R> ArbitraryTransformer<E, R> transformer(
			Supplier<String> name,
			Function<? super Arbitrary<E>, ? extends Arbitrary<R>> action
		) {
			return new ArbitraryTransformer<E, R>() {
				@Override
				public String toString() {
					return name.get();
				}

				@Override
				public Arbitrary<R> apply(Arbitrary<E> input) {
					return action.apply(input);
				}
			};
		}

		static Object wrapWithOpaqueObject(Object that) {
			return new Object() {
				@Override
				public String toString() {
					return JqwikStringSupport.displayString(that);
				}
			};
		}
	}

	@Provide
	default Arbitrary<ArbitraryTransformer<?, ?>> arbitraryTransformations() {
		return Arbitraries.of(
			ArbitraryTransformer.transformer(
				() -> "identity",
				(x) -> x
			),
			ArbitraryTransformer.transformer(
				() -> ".set()",
				(x) -> x.set()
			),
			ArbitraryTransformer.transformer(
				() -> ".set().mapEach(::identity)",
				(x) -> x.set().mapEach((set, value) -> value)
			),
			ArbitraryTransformer.transformer(
				() -> ".set().flatMapEach(just(value))",
				(x) -> x.set().flatMapEach((set, value) -> Arbitraries.just(value))
			),
			ArbitraryTransformer.transformer(
				() -> ".map(wrapWithObject)",
				(x) -> x.map(ArbitraryTransformer::wrapWithOpaqueObject)
			),
			ArbitraryTransformer.transformer(
				() -> ".map(wrapWithObject).set()",
				(x) -> x.map(ArbitraryTransformer::wrapWithOpaqueObject).set()
			),
			ArbitraryTransformer.transformer(
				() -> ".map(wrapWithObject).set().mapEach(::identity)",
				(x) -> x.map(ArbitraryTransformer::wrapWithOpaqueObject).set().mapEach((set, value) -> value)
			),
			ArbitraryTransformer.transformer(
				() -> ".map(wrapWithObject).set().flatMapEach(just(value))",
				(x) -> x.map(ArbitraryTransformer::wrapWithOpaqueObject).set().flatMapEach((set, value) -> Arbitraries.just(value))
			)
		);
	}
}
