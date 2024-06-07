package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.edgeCases.*;
import net.jqwik.api.statistics.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class SetArbitraryTests {

	@Example
	void set(@ForAll Random random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		SetArbitrary<Integer> setArbitrary = integerArbitrary.set().ofMinSize(2).ofMaxSize(7);

		RandomGenerator<Set<Integer>> generator = setArbitrary.generator(1, true);

		assertGeneratedSet(generator, random, 2, 7);
	}

	@Example
	void largeSetOfFixedSize(@ForAll Random random) {
		int size = 1000;
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers();
		SetArbitrary<Integer> setArbitrary = integerArbitrary.set().ofSize(size);

		RandomGenerator<Set<Integer>> generator = setArbitrary.generator(1000, true);

		for (int i = 0; i < 5; i++) { // assertAllGenerated generates too many samples
			Set<Integer> set = generator.next(random).value();
			assertThat(set.size()).isEqualTo(size);
		}
	}

	@Example
	void setWithLessElementsThanMaxSize(@ForAll Random random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.of(1, 2, 3, 4, 5);
		SetArbitrary<Integer> setArbitrary = integerArbitrary.set().ofMinSize(2);

		RandomGenerator<Set<Integer>> generator = setArbitrary.generator(1, true);

		assertGeneratedSet(generator, random, 2, 5);
	}

	@Example
	void mapEach(@ForAll Random random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		@SuppressWarnings("unchecked")
		Arbitrary<Set<Tuple.Tuple2<Integer, Set<Integer>>>> setArbitrary =
			integerArbitrary
				.set().ofSize(5)
				.mapEach((all, each) -> Tuple.of(each, (Set<Integer>) all));

		RandomGenerator<Set<Tuple.Tuple2<Integer, Set<Integer>>>> generator = setArbitrary.generator(1, true);

		assertAllGenerated(
			generator,
			random,
			set -> {
				assertThat(set).hasSize(5);
				assertThat(set).allMatch(tuple -> tuple.get2().size() == 5);
			}
		);
	}

	@Example
	void flatMapEach(@ForAll Random random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		Arbitrary<Set<Tuple.Tuple2<Integer, Integer>>> setArbitrary =
			integerArbitrary
				.set().ofSize(5)
				.flatMapEach((all, each) ->
								 Arbitraries.of(all).map(friend -> Tuple.of(each, friend))
				);

		RandomGenerator<Set<Tuple.Tuple2<Integer, Integer>>> generator = setArbitrary.generator(1, true);

		assertAllGenerated(
			generator,
			random,
			set -> {
				assertThat(set).hasSize(5);
				assertThat(set).allMatch(tuple -> tuple.get2() <= 10);
			}
		);
	}

	@Example
	void multipleUniquenessConstraints(@ForAll Random random) {
		SetArbitrary<Integer> setArbitrary =
			Arbitraries.integers().between(1, 1000).set().ofMaxSize(20)
					   .uniqueElements(i -> i % 99)
					   .uniqueElements(i -> i % 100);

		RandomGenerator<Set<Integer>> generator = setArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, set -> {
			assertThat(isUniqueModulo(set, 100)).isTrue();
			assertThat(isUniqueModulo(set, 99)).isTrue();
		});
	}

	@Example
	@StatisticsReport(onFailureOnly = true)
	void withSizeDistribution(@ForAll Random random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers();
		SetArbitrary<Integer> arbitrary =
			integerArbitrary.set().ofMaxSize(100)
							.withSizeDistribution(RandomDistribution.uniform());

		RandomGenerator<Set<Integer>> generator = arbitrary.generator(1, false);

		for (int i = 0; i < 5000; i++) {
			Set<Integer> list = generator.next(random).value();
			Statistics.collect(list.size());
		}

		Statistics.coverage(checker -> {
			for (int size = 0; size <= 100; size++) {
				checker.check(size).percentage(p -> p >= 0.4);
			}
		});
	}

	private boolean isUniqueModulo(Set<Integer> list, int modulo) {
		List<Integer> moduloList = list.stream().map(i -> {
			if (i == null) {
				return null;
			}
			return i % modulo;
		}).collect(Collectors.toList());
		return new LinkedHashSet<>(moduloList).size() == list.size();
	}

	@Group
	class ExhaustiveGeneration {
		@Example
		void setsAreCombinationsOfElementsUpToMaxLength() {
			Optional<ExhaustiveGenerator<Set<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 3).set().ofMaxSize(2).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Set<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(7);
			assertThat(generator).containsExactly(
				asSet(),
				asSet(1),
				asSet(2),
				asSet(3),
				asSet(1, 2),
				asSet(1, 3),
				asSet(2, 3)
			);
		}

		@Example
		void combinationsAreFilteredByUniquenessConstraints() {
			Optional<ExhaustiveGenerator<Set<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 4).set().ofMaxSize(2).uniqueElements(i -> i % 3)
						   .exhaustive();

			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Set<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(11);
			assertThat(generator).containsExactlyInAnyOrder(
				asSet(),
				asSet(1),
				asSet(2),
				asSet(3),
				asSet(4),
				asSet(1, 2),
				asSet(1, 3),
				asSet(2, 3),
				asSet(2, 4),
				asSet(3, 4)
			);
		}

		@Example
		void lessElementsThanSetSize() {
			Optional<ExhaustiveGenerator<Set<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 2).set().ofMaxSize(5).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Set<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(
				asSet(),
				asSet(1),
				asSet(2),
				asSet(1, 2)
			);
		}

		private Set<Integer> asSet(Integer... ints) {
			return new LinkedHashSet<>(asList(ints));
		}

		@Example
		void elementArbitraryNotExhaustive() {
			Optional<ExhaustiveGenerator<Set<Double>>> optionalGenerator =
				Arbitraries.doubles().between(1, 10).set().ofMaxSize(1).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

		@Example
		void tooManyCombinations() {
			Optional<ExhaustiveGenerator<Set<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 75).set().ofMaxSize(10).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}
	}

	@Group
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set();
			return Arbitraries.of(arbitrary);
		}
	}

	@Group
	class EdgeCasesGeneration implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set();
			return Arbitraries.of(arbitrary);
		}

		@Example
		void setEdgeCases() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set();
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				Collections.emptySet(),
				Collections.singleton(-10),
				Collections.singleton(-9),
				Collections.singleton(-2),
				Collections.singleton(-1),
				Collections.singleton(0),
				Collections.singleton(1),
				Collections.singleton(2),
				Collections.singleton(9),
				Collections.singleton(10)
			);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).hasSize(10);
		}

		@Example
		void setEdgeCasesWithMinSize1() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set().ofMinSize(1);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				Collections.singleton(-10),
				Collections.singleton(-9),
				Collections.singleton(-2),
				Collections.singleton(-1),
				Collections.singleton(0),
				Collections.singleton(1),
				Collections.singleton(2),
				Collections.singleton(9),
				Collections.singleton(10)
			);
		}

		@Example
		void edgeCasesAreFilteredByUniquenessConstraints() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set().ofSize(2)
													.uniqueElements(i -> i % 2);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).isEmpty();
		}
	}

	@Group
	@PropertyDefaults(tries = 100)
	class Shrinking {

		@Property
		void shrinksToEmptySetByDefault(@ForAll Random random) {
			SetArbitrary<Integer> sets = Arbitraries.integers().between(1, 10).set();
			Set<Integer> value = falsifyThenShrink(sets, random);
			assertThat(value).isEmpty();
		}

		@Property
		void shrinkToMinSize(@ForAll Random random, @ForAll @IntRange(min = 1, max = 20) int min) {
			SetArbitrary<Integer> sets = Arbitraries.integers().between(1, 100).set().ofMinSize(min);
			Set<Integer> value = falsifyThenShrink(sets, random);
			assertThat(value).hasSize(min);
			List<Integer> smallestElements = IntStream.rangeClosed(1, min).boxed().collect(Collectors.toList());
			assertThat(value).containsExactlyInAnyOrderElementsOf(smallestElements);
		}

		@Property
		void shrinkWithUniqueness(@ForAll Random random, @ForAll @IntRange(min = 2, max = 9) int min) {
			SetArbitrary<Integer> lists =
				Arbitraries.integers().between(1, 1000).set().ofMinSize(min).ofMaxSize(9)
						   .uniqueElements(i -> i % 10);
			Set<Integer> value = falsifyThenShrink(lists, random);
			assertThat(value).hasSize(min);
			assertThat(isUniqueModulo(value, 10))
				.describedAs("%s is not unique mod 10", value)
				.isTrue();
			assertThat(value).allMatch(i -> i <= min);
		}

	}

	private void assertGeneratedSet(RandomGenerator<Set<Integer>> generator, Random random, int minSize, int maxSize) {
		assertAllGenerated(generator, random, set -> {
			assertThat(set.size()).isBetween(minSize, maxSize);
			assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		});
	}

}
