package net.jqwik.api;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.constraints.*;
import net.jqwik.engine.*;
import net.jqwik.engine.facades.*;
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

	@Property(tries = 100)
	default void sameRandomWillGenerateSameValueOnFreshGenerator(
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

	@Property(tries = 100)
	//@Disabled("Fix equals() methods in all arbitraries")
	default void memoizableArbitrariesWillMemoizeGenerators(
		@ForAll Random randomToGenerateArbitrary,
		@ForAll @IntRange(min = 1, max = 10000) int genSize,
		@ForAll boolean withEdgeCases
	) {
		long seedToGenerateArbitraries = randomToGenerateArbitrary.nextLong();
		Arbitrary<?> arbitrary1 = arbitraries().generator(1000).next(SourceOfRandomness.newRandom(seedToGenerateArbitraries)).value();
		Arbitrary<?> arbitrary2 = arbitraries().generator(1000).next(SourceOfRandomness.newRandom(seedToGenerateArbitraries)).value();

		assertThat(arbitrary1.isGeneratorMemoizable()).isEqualTo(arbitrary2.isGeneratorMemoizable());

		if (!arbitrary1.isGeneratorMemoizable()) {
			return;
		}

		RandomGenerator<?> gen1 = Memoize.memoizedGenerator(
			arbitrary1, genSize, withEdgeCases,
			() -> arbitrary1.generator(genSize, withEdgeCases)
		);
		RandomGenerator<?> gen2 = Memoize.memoizedGenerator(
			arbitrary2, genSize, withEdgeCases,
			() -> arbitrary2.generator(genSize, withEdgeCases)
		);
		assertThat(gen1).isSameAs(gen2);
	}

	@Property(tries = 100)
	default void sameRandomWillGenerateSameValueOnMemoizedGenerator(
		@ForAll Random randomToGenerateArbitrary,
		@ForAll Random randomToGenerateValue,
		@ForAll @IntRange(min = 1, max = 10000) int genSize,
		@ForAll boolean withEdgeCases
	) {
		long seedToGenerateArbitraries = randomToGenerateArbitrary.nextLong();
		Arbitrary<?> arbitrary1 = arbitraries().generator(1000).next(SourceOfRandomness.newRandom(seedToGenerateArbitraries)).value();
		Arbitrary<?> arbitrary2 = arbitraries().generator(1000).next(SourceOfRandomness.newRandom(seedToGenerateArbitraries)).value();

		long seedToGenerateValue = randomToGenerateValue.nextLong();

		RandomGenerator<?> gen1 = Memoize.memoizedGenerator(arbitrary1, genSize, withEdgeCases, () -> arbitrary1.generator(genSize, withEdgeCases));
		RandomGenerator<?> gen2 = Memoize.memoizedGenerator(arbitrary2, genSize, withEdgeCases, () -> arbitrary2.generator(genSize, withEdgeCases));

		Object valueA = gen1.next(SourceOfRandomness.newRandom(seedToGenerateValue)).value();
		Object valueB = gen2.next(SourceOfRandomness.newRandom(seedToGenerateValue)).value();
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
