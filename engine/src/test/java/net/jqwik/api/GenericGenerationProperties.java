package net.jqwik.api;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.constraints.*;
import net.jqwik.engine.*;
import net.jqwik.engine.support.*;

import static org.assertj.core.api.Assertions.*;

public interface GenericGenerationProperties {

	Arbitrary<Arbitrary<?>> arbitraries();

	@Provide("arbitraries")
	default Arbitrary<Arbitrary<?>> transformedArbitraries(
		@ForAll("arbitraryTransformations") Function<Arbitrary<?>, Arbitrary<?>> transformation
	) {
		return arbitraries().map(transformation);
	}

	@Property
	default void sameRandomValueWillAlwaysGenerateSameValues(
		@ForAll("arbitraries") Arbitrary<?> arbitrary,
		@ForAll Random random,
		@ForAll @IntRange(min = 1, max = 10000) int genSize,
		@ForAll boolean withEdgeCases
	) {
		long seed = random.nextLong();

		// Values should be equal with respect to "displayString"
		// because some objects don't have a meaningful equals() method
		RandomGenerator<?> gen1 = arbitrary.generator(genSize, withEdgeCases);
		Object valueA = gen1.next(SourceOfRandomness.newRandom(seed)).value();
		RandomGenerator<?> gen2 = arbitrary.generator(genSize, withEdgeCases);
		Object valueB = gen2.next(SourceOfRandomness.newRandom(seed)).value();
		assertThat(JqwikStringSupport.displayString(valueA))
			.isEqualTo(JqwikStringSupport.displayString(valueB));
	}

	interface ArbitraryTransformer extends Function<Arbitrary<?>, Arbitrary<?>> {
		static ArbitraryTransformer transformer(
			String name,
			Function<Arbitrary<?>, Arbitrary<?>> action
		) {
			return new ArbitraryTransformer() {
				@Override
				public String toString() {
					return name;
				}

				@Override
				public Arbitrary<?> apply(Arbitrary<?> input) {
					return action.apply(input);
				}
			};
		}

		default ArbitraryTransformer then(ArbitraryTransformer after) {
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

		ArbitraryTransformer IDENTITY = ArbitraryTransformer.transformer(
			"identity",
			(x) -> x
		);

		ArbitraryTransformer WRAP_WITH_OPAQUE_OBJECT = ArbitraryTransformer.transformer(
			".map(::wrapWithOpaqueObject)",
			(x) -> x.map(ArbitraryTransformer::wrapWithOpaqueObject)
		);

		ArbitraryTransformer SET = ArbitraryTransformer.transformer(
			".set()",
			(x) -> x.set()
		);

		ArbitraryTransformer SET__MAP_EACH = ArbitraryTransformer.transformer(
			".mapEach(::identity)",
			(x) -> x.set().mapEach((set, value) -> value)
		);

		ArbitraryTransformer SET__FLAT_MAP_EACH = ArbitraryTransformer.transformer(
			".set().flatMapEach(just(value))",
			(x) -> x.set().flatMapEach((set, value) -> Arbitraries.just(value))
		);
	}

	@Provide
	static Arbitrary<ArbitraryTransformer> arbitraryTransformations() {
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
