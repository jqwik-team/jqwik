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
		@ForAll("arbitraryTransformations") ArbitraryTransformer<?, ?> transformation,
		@ForAll Random random,
		@ForAll @IntRange(min = 1, max = 20) int size,
		@ForAll @IntRange(min = 1, max = 1000) int genSize,
		@ForAll boolean withEdgeCases
	) {
		@SuppressWarnings({"unchecked", "rawtypes"})
		Arbitrary<?> values = transformation.apply((Arbitrary) arbitrary);
		RandomGenerator<?> gen = values.generator(genSize, withEdgeCases);
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
			String name,
			Function<? super Arbitrary<E>, ? extends Arbitrary<R>> action
		) {
			return new ArbitraryTransformer<E, R>() {
				@Override
				public String toString() {
					return name;
				}

				@Override
				public Arbitrary<R> apply(Arbitrary<E> input) {
					return action.apply(input);
				}
			};
		}

		default <K> ArbitraryTransformer<U, K> then(ArbitraryTransformer<V, K> after) {
			return transformer(
				toString() + after,
				(x) -> after.apply(apply(x))
			);
		}

		static Object wrapWithOpaqueObject(Object that) {
			return new Object() {
				@Override
				public String toString() {
					return JqwikStringSupport.displayString(that);
				}
			};
		}

		ArbitraryTransformer<Object, Object> IDENTITY = ArbitraryTransformer.transformer(
			"identity",
			(x) -> x
		);

		ArbitraryTransformer<Object, Object> WRAP_WITH_OPAQUE_OBJECT = ArbitraryTransformer.transformer(
			".map(::wrapWithOpaqueObject)",
			(x) -> x.map(ArbitraryTransformer::wrapWithOpaqueObject)
		);

		ArbitraryTransformer<Object, Set<Object>> SET = ArbitraryTransformer.transformer(
			".set()",
			(x) -> x.set()
		);

		ArbitraryTransformer<Object, Set<Object>> SET__MAP_EACH = ArbitraryTransformer.transformer(
			".mapEach(::identity)",
			(x) -> x.set().mapEach((set, value) -> value)
		);

		ArbitraryTransformer<Object, Set<Object>> SET__FLAT_MAP_EACH = ArbitraryTransformer.transformer(
			".set().flatMapEach(just(value))",
			(x) -> x.set().flatMapEach((set, value) -> Arbitraries.just(value))
		);
	}

	@Provide
	static Arbitrary<ArbitraryTransformer<?, ?>> arbitraryTransformations() {
		return Arbitraries.of(
			ArbitraryTransformer.IDENTITY,
			ArbitraryTransformer.WRAP_WITH_OPAQUE_OBJECT,
			ArbitraryTransformer.SET,
			ArbitraryTransformer.SET__MAP_EACH,
			ArbitraryTransformer.SET__FLAT_MAP_EACH,
			ArbitraryTransformer.WRAP_WITH_OPAQUE_OBJECT.then(ArbitraryTransformer.SET),
			ArbitraryTransformer.WRAP_WITH_OPAQUE_OBJECT.then(ArbitraryTransformer.SET__MAP_EACH),
			ArbitraryTransformer.WRAP_WITH_OPAQUE_OBJECT.then(ArbitraryTransformer.SET__FLAT_MAP_EACH)
		);
	}
}
