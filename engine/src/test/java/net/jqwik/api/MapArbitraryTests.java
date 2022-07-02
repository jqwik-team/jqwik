package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.edgeCases.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

class MapArbitraryTests {

	@Example
	void map(@ForAll Random random) {
		Arbitrary<Integer> keys = Arbitraries.integers().between(1, 10);
		Arbitrary<String> values = Arbitraries.strings().alpha().ofLength(5);

		MapArbitrary<Integer, String> mapArbitrary = Arbitraries.maps(keys, values).ofMinSize(0).ofMaxSize(10);

		RandomGenerator<Map<Integer, String>> generator = mapArbitrary.generator(1, true);

		assertAllGenerated(
			generator,
			random,
			map -> {
				assertThat(map.size()).isBetween(0, 10);
				if (map.isEmpty()) return;
				assertThat(map.keySet()).containsAnyOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
				assertThat(map.values()).allMatch(value -> value.length() == 5);
			}
		);

		TestingSupport.checkAtLeastOneGenerated(generator, random, Map::isEmpty);
		TestingSupport.checkAtLeastOneGenerated(generator, random, map -> map.size() == 10);

		// Generated maps are mutable
		assertAllGenerated(
			generator,
			random,
			map -> {
				int sizeBefore = map.size();
				map.put(42, "fortytwo");
				assertThat(map.size()).isEqualTo(sizeBefore + 1);
			}
		);
	}

	@Example
	void mapWithLessElementsThanMaxSize(@ForAll Random random) {
		Arbitrary<Integer> keys = Arbitraries.integers().between(1, 3);
		Arbitrary<String> values = Arbitraries.strings().alpha().ofLength(5);

		MapArbitrary<Integer, String> mapArbitrary = Arbitraries.maps(keys, values);
		RandomGenerator<Map<Integer, String>> generator = mapArbitrary.generator(1, true);

		assertAllGenerated(
			generator,
			random,
			map -> {assertThat(map.size()).isBetween(0, 3);}
		);

		TestingSupport.checkAtLeastOneGenerated(generator, random, Map::isEmpty);
		TestingSupport.checkAtLeastOneGenerated(generator, random, map -> map.size() == 3);
	}

	@Example
	@StatisticsReport(StatisticsReport.StatisticsReportMode.OFF)
	void withSizeDistribution(@ForAll Random random) {
		Arbitrary<Integer> keys = Arbitraries.integers();
		Arbitrary<String> values = Arbitraries.strings().alpha().ofLength(5);

		MapArbitrary<Integer, String> arbitrary =
			Arbitraries.maps(keys, values).ofMaxSize(50)
					   .withSizeDistribution(RandomDistribution.uniform());

		RandomGenerator<Map<Integer, String>> generator = arbitrary.generator(1, false);

		for (int i = 0; i < 1000; i++) {
			Map<Integer, String> map = generator.next(random).value();
			Statistics.collect(map.size());
		}

		Statistics.coverage(checker -> {
			for (int size = 0; size <= 50; size++) {
				checker.check(size).percentage(p -> p >= 0.4);
			}
		});
	}

	@Example
	void keyUniqueness(@ForAll Random random) {
		MapArbitrary<Integer, String> mapArbitrary =
			Arbitraries.maps(
				Arbitraries.integers().between(1, 1000),
				Arbitraries.strings().alpha().ofMaxLength(10)
			).ofMinSize(2).ofMaxSize(10).uniqueKeys(i -> i % 100);

		RandomGenerator<Map<Integer, String>> generator = mapArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, map -> {
			assertThat(isUniqueModulo(map.keySet(), 100)).isTrue();
		});
	}

	@Example
	void valueUniqueness(@ForAll Random random) {
		MapArbitrary<String, Integer> mapArbitrary =
			Arbitraries.maps(
				Arbitraries.strings().alpha().ofMaxLength(10),
				Arbitraries.integers().between(1, 1000)
			).ofMinSize(2).ofMaxSize(10).uniqueValues(i -> i % 100);

		RandomGenerator<Map<String, Integer>> generator = mapArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, map -> {
			assertThat(isUniqueModulo(map.values(), 100)).isTrue();
		});
	}

	@Example
	void uniqueValues(@ForAll Random random) {
		MapArbitrary<String, Integer> mapArbitrary =
			Arbitraries.maps(
				Arbitraries.strings().alpha().ofMaxLength(10),
				Arbitraries.integers().between(1, 10)
			).ofMinSize(2).ofMaxSize(10).uniqueValues();

		RandomGenerator<Map<String, Integer>> generator = mapArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, map -> {
			assertThat(isUniqueModulo(map.values(), 10)).isTrue();
		});
	}

	private boolean isUniqueModulo(Collection<Integer> list, int modulo) {
		List<Integer> moduloList = list.stream().map(i -> {
			if (i == null) {
				return null;
			}
			return i % modulo;
		}).collect(Collectors.toList());
		return new LinkedHashSet<>(moduloList).size() == list.size();
	}

	@Group
	@PropertyDefaults(tries = 100)
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			StringArbitrary keys = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1);
			Arbitrary<Integer> values = Arbitraries.of(10, 100);
			Arbitrary<Map<String, Integer>> arbitrary = Arbitraries.maps(keys, values);
			return Arbitraries.of(arbitrary);
		}
	}

	@Group
	class EdgeCasesGeneration implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			StringArbitrary keys = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1);
			Arbitrary<Integer> values = Arbitraries.of(10, 100);
			Arbitrary<Map<String, Integer>> arbitrary = Arbitraries.maps(keys, values);
			return Arbitraries.of(arbitrary);
		}

		@Example
		void edgeCases() {
			StringArbitrary keys = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1);
			Arbitrary<Integer> values = Arbitraries.of(10, 100);
			Arbitrary<Map<String, Integer>> arbitrary = Arbitraries.maps(keys, values);
			EdgeCases<Map<String, Integer>> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				Collections.emptyMap(),
				Collections.singletonMap("a", 10),
				Collections.singletonMap("a", 100),
				Collections.singletonMap("z", 10),
				Collections.singletonMap("z", 100)
			);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(5);
		}

		@Example
		void edgeCasesCanBeShrunk() {
			StringArbitrary keys = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1);
			Arbitrary<Integer> values = Arbitraries.of(10, 100);
			Arbitrary<Map<String, Integer>> arbitrary = Arbitraries.maps(keys, values);
			EdgeCases<Map<String, Integer>> edgeCases = arbitrary.edgeCases();

			Set<Shrinkable<Map<String, Integer>>> shrinkables = collectEdgeCaseShrinkables(edgeCases);
			for (Shrinkable<Map<String, Integer>> shrinkable : shrinkables) {
				Map<String, Integer> shrunkValue = shrink(shrinkable, TestingFalsifier.alwaysFalsify(), null);
				assertThat(shrunkValue).isEmpty();
			}
		}
	}

	@Group
	class ExhaustiveTesting {

		@Example
		void allCombinationsOfKeysAndValues() {

			IntegerArbitrary keys = Arbitraries.integers().between(1, 3);
			IntegerArbitrary values = Arbitraries.integers().between(4, 5);
			Optional<ExhaustiveGenerator<Map<Integer, Integer>>> mapGenerator =
				Arbitraries.maps(keys, values).ofSize(2).exhaustive();
			assertThat(mapGenerator).isPresent();

			ExhaustiveGenerator<Map<Integer, Integer>> generator = mapGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(12);

			assertThat(generator).containsExactlyInAnyOrder(
				createMap(Tuple.of(1, 5), Tuple.of(2, 5)),
				createMap(Tuple.of(1, 4), Tuple.of(2, 4)),
				createMap(Tuple.of(1, 5), Tuple.of(2, 4)),
				createMap(Tuple.of(1, 4), Tuple.of(2, 5)),
				createMap(Tuple.of(1, 5), Tuple.of(3, 5)),
				createMap(Tuple.of(1, 4), Tuple.of(3, 4)),
				createMap(Tuple.of(1, 5), Tuple.of(3, 4)),
				createMap(Tuple.of(1, 4), Tuple.of(3, 5)),
				createMap(Tuple.of(2, 5), Tuple.of(3, 5)),
				createMap(Tuple.of(2, 4), Tuple.of(3, 4)),
				createMap(Tuple.of(2, 5), Tuple.of(3, 4)),
				createMap(Tuple.of(2, 4), Tuple.of(3, 5))
			);
		}

		@SafeVarargs
		private final <T, U> Map<T, U> createMap(Tuple.Tuple2<T, U>... tuples) {
			HashMap<T, U> result = new LinkedHashMap<>();
			for (Tuple.Tuple2<T, U> tuple : tuples) {
				result.put(tuple.get1(), tuple.get2());
			}
			return result;
		}

		@Example
		void combinationsAreFilteredByKeyUniquenessConstraints() {

			IntegerArbitrary keys = Arbitraries.integers().between(1, 3);
			IntegerArbitrary values = Arbitraries.integers().between(4, 5);
			Optional<ExhaustiveGenerator<Map<Integer, Integer>>> mapGenerator =
				Arbitraries.maps(keys, values).ofSize(2).uniqueKeys(i -> i % 2)
						   .exhaustive();
			assertThat(mapGenerator).isPresent();

			ExhaustiveGenerator<Map<Integer, Integer>> generator = mapGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(8);

			assertThat(generator).containsExactlyInAnyOrder(
				createMap(Tuple.of(1, 5), Tuple.of(2, 5)),
				createMap(Tuple.of(1, 4), Tuple.of(2, 4)),
				createMap(Tuple.of(1, 5), Tuple.of(2, 4)),
				createMap(Tuple.of(1, 4), Tuple.of(2, 5)),
				createMap(Tuple.of(2, 5), Tuple.of(3, 5)),
				createMap(Tuple.of(2, 4), Tuple.of(3, 4)),
				createMap(Tuple.of(2, 5), Tuple.of(3, 4)),
				createMap(Tuple.of(2, 4), Tuple.of(3, 5))
			);
		}

		@Example
		void combinationsAreFilteredByValueUniquenessConstraints() {

			IntegerArbitrary keys = Arbitraries.integers().between(4, 5);
			IntegerArbitrary values = Arbitraries.integers().between(1, 3);
			Optional<ExhaustiveGenerator<Map<Integer, Integer>>> mapGenerator =
				Arbitraries.maps(keys, values).ofSize(2).uniqueValues(i -> i % 2)
						   .exhaustive();
			assertThat(mapGenerator).isPresent();

			ExhaustiveGenerator<Map<Integer, Integer>> generator = mapGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(9);

			assertThat(generator).containsExactlyInAnyOrder(
				createMap(Tuple.of(4, 1), Tuple.of(5, 2)),
				createMap(Tuple.of(4, 2), Tuple.of(5, 1)),
				createMap(Tuple.of(4, 2), Tuple.of(5, 3)),
				createMap(Tuple.of(4, 3), Tuple.of(5, 2))
			);
		}

		@Example
		void tooManyCombinations() {
			IntegerArbitrary keys = Arbitraries.integers().between(1, 1000);
			IntegerArbitrary values = Arbitraries.integers().between(1000, 2000);
			Optional<ExhaustiveGenerator<Optional<Map<Integer, Integer>>>> optionalGenerator =
				Arbitraries.maps(keys, values).ofMaxSize(10).optional().exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}
	}

	@Group
	class Shrinking {

		@Property(tries = 10)
		void mapIsShrunkToEmptyMap(@ForAll Random random) {
			Arbitrary<Integer> keys = Arbitraries.integers().between(-10, 10);
			Arbitrary<String> values = Arbitraries.strings().alpha().ofLength(1);

			SizableArbitrary<Map<Integer, String>> arbitrary = Arbitraries.maps(keys, values).ofMaxSize(10);

			Map<Integer, String> shrunkValue = falsifyThenShrink(arbitrary, random);
			assertThat(shrunkValue).isEmpty();
		}

		@Property(tries = 10)
		void mapIsShrunkToSmallestValue(@ForAll Random random) {
			Arbitrary<Integer> keys = Arbitraries.integers().between(-10, 10);
			Arbitrary<String> values = Arbitraries.strings().withCharRange('A', 'Z').ofLength(1);

			SizableArbitrary<Map<Integer, String>> arbitrary = Arbitraries.maps(keys, values).ofMaxSize(10);

			TestingFalsifier<Map<Integer, String>> sumOfKeysLessThan2 = map -> map.keySet().size() < 2;
			Map<Integer, String> map = falsifyThenShrink(arbitrary, random, sumOfKeysLessThan2);

			assertThat(map).hasSize(2);
			assertThat(map.keySet()).containsAnyOf(0, 1, -1);
			assertThat(map.values()).containsOnly("A");
		}

	}
}
