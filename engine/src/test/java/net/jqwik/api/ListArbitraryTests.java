package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.jupiter.api.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.edgeCases.*;
import net.jqwik.api.statistics.*;
import net.jqwik.engine.*;
import net.jqwik.testing.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@StatisticsReport(onFailureOnly = true)
class ListArbitraryTests {

	public static final int DEFAULT_MAX_SIZE = 255;

	@Example
	void listWithDefaultSizes() {
		Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
		ListArbitrary<String> listArbitrary = stringArbitrary.list();

		RandomGenerator<List<String>> generator = listArbitrary.generator(1, true);
		assertGeneratedLists(generator, 0, DEFAULT_MAX_SIZE);
	}

	@Example
	void ofSize() {
		Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
		ListArbitrary<String> listArbitrary = stringArbitrary.list().ofSize(42);

		RandomGenerator<List<String>> generator = listArbitrary.generator(1, true);
		assertGeneratedLists(generator, 42, 42);
	}

	@Example
	void ofMinSize_ofMaxSize() {
		Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
		ListArbitrary<String> listArbitrary = stringArbitrary.list().ofMinSize(2).ofMaxSize(5);

		RandomGenerator<List<String>> generator = listArbitrary.generator(1, true);
		assertGeneratedLists(generator, 2, 5);
	}

	@Example
	void ofMinSize_aboveDefaultMaxSize() {
		Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
		int minSize = DEFAULT_MAX_SIZE + 1;
		ListArbitrary<String> listArbitrary = stringArbitrary.list().ofMinSize(minSize);

		RandomGenerator<List<String>> generator = listArbitrary.generator(1, true);
		assertGeneratedLists(generator, minSize, minSize * 2);
	}

	// Lists of that size often lead to OutOfMemoryError
	// @Property(tries = 10)
	void ofMinSize_closeToIntegerMax(@ForAll @IntRange(min = Integer.MAX_VALUE / 2) int minSize, @ForAll JqwikRandom random) {
		ListArbitrary<Integer> listArbitrary = Arbitraries.of(1,2,3).list().ofMinSize(minSize);
		RandomGenerator<List<Integer>> generator = listArbitrary.generator(1, true);

		Shrinkable<List<Integer>> list = generator.next(random);
		assertThat(list.value()).hasSizeBetween(minSize, Integer.MAX_VALUE);
	}

	@Example
	void reduceList(@ForAll JqwikRandom random) {
		ListArbitrary<Integer> listArbitrary =
			Arbitraries.integers().between(1, 5).list().ofMinSize(1).ofMaxSize(10);

		Arbitrary<Integer> integerArbitrary = listArbitrary.reduce(0, Integer::sum);

		RandomGenerator<Integer> generator = integerArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, sum -> {
			assertThat(sum).isBetween(1, 50);
		});

		TestingSupport.checkAtLeastOneGenerated(generator, random, sum -> sum == 1);
		TestingSupport.checkAtLeastOneGenerated(generator, random, sum -> sum > 30);
	}

	@Example
	void uniqueElements(@ForAll JqwikRandom random) {
		ListArbitrary<Integer> listArbitrary =
			Arbitraries.integers().between(1, 1000).list().ofMaxSize(20)
					   .uniqueElements(i -> i % 100);

		RandomGenerator<List<Integer>> generator = listArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, list -> {
			assertThat(isUniqueModulo(list, 100)).isTrue();
		});
	}

	@Property(tries = 10)
	void uniqueElementsWithoutMaxSize(@ForAll JqwikRandom random, @ForAll @IntRange(max = 10) int minSize) {
		ListArbitrary<Integer> listArbitrary =
			Arbitraries.integers().between(1, 1000).list().ofMinSize(minSize)
					   .uniqueElements(i -> i % 10);

		RandomGenerator<List<Integer>> generator = listArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, list -> {
			assertThat(isUniqueModulo(list, 10)).isTrue();
		});
	}

	@Example
	void uniqueElementsWithNull(@ForAll JqwikRandom random) {
		ListArbitrary<Integer> listArbitrary =
			Arbitraries.integers().between(1, 1000).injectNull(0.5)
					   .list().ofMaxSize(20)
					   .uniqueElements(i -> i % 100);

		RandomGenerator<List<Integer>> generator = listArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, list -> {
			assertThat(isUniqueModulo(list, 100)).isTrue();
		});
	}

	@Example
	void multipleUniquenessConstraints(@ForAll JqwikRandom random) {
		ListArbitrary<Integer> listArbitrary =
			Arbitraries.integers().between(1, 1000).list().ofMaxSize(20)
					   .uniqueElements(i -> i % 99)
					   .uniqueElements(i -> i % 100);

		RandomGenerator<List<Integer>> generator = listArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, list -> {
			assertThat(isUniqueModulo(list, 100)).isTrue();
			assertThat(isUniqueModulo(list, 99)).isTrue();
		});
	}

	@Example
	void uniquenessConstraintCannotBeFulfilled(@ForAll JqwikRandom random) {
		ListArbitrary<Integer> listArbitrary =
			Arbitraries.integers().between(1, 1000).list().ofSize(10)
					   .uniqueElements(i -> i % 5);

		RandomGenerator<List<Integer>> generator = listArbitrary.generator(1000, true);

		Assertions.assertThrows(TooManyFilterMissesException.class, () -> generator.next(random));
	}

	@Example
	void uniquenessElements(@ForAll JqwikRandom random) {
		ListArbitrary<Integer> listArbitrary =
			Arbitraries.integers().between(1, 1000).list().ofMaxSize(20).uniqueElements();

		RandomGenerator<List<Integer>> generator = listArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, list -> {
			assertThat(isUniqueModulo(list, 1000)).isTrue();
		});
	}

	@Example
	void uniqueListsAreSometimesGeneratedByDefault(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(0, 1_000);
		ListArbitrary<Integer> listArbitrary = integerArbitrary.list().ofSize(50);

		RandomGenerator<List<Integer>> generator = listArbitrary.generator(1, false);

		for (int i = 0; i < 2000; i++) {
			List<Integer> list = generator.next(random).value();
			Statistics.collect(isUniqueModulo(list, 1_000));
		}

		Statistics.coverage(checker -> {
			checker.check(true).percentage(p -> p >= 1.5);
		});
	}

	@Example
	void uniqueListsAreSometimesGeneratedByDefaultEvenIfMaxSizeDoesNotAllowThat(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(0, 10);
		ListArbitrary<Integer> listArbitrary = integerArbitrary.list().ofMaxSize(50);

		RandomGenerator<List<Integer>> generator = listArbitrary.generator(1, false);

		for (int i = 0; i < 2000; i++) {
			List<Integer> list = generator.next(random).value();
			Statistics.collect(isUniqueModulo(list, 10));
		}

		Statistics.coverage(checker -> {
			checker.check(true).percentage(p -> p >= 1.5);
		});
	}

	@Group
	class SizeDistribution {

		@Example
		void use_explicit_size_distribution(@ForAll JqwikRandom random) {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers();
			ListArbitrary<Integer> arbitrary =
				integerArbitrary.list().ofMaxSize(100)
								.withSizeDistribution(RandomDistribution.uniform());

			RandomGenerator<List<Integer>> generator = arbitrary.generator(1, false);

			for (int i = 0; i < 5000; i++) {
				List<Integer> list = generator.next(random).value();
				Statistics.collect(list.size());
			}

			Statistics.coverage(checker -> {
				for (int size = 0; size <= 100; size++) {
					checker.check(size).percentage(p -> p >= 0.4);
				}
			});
		}

		@Example
		void without_explicit_size_distribution_each_possible_size_should_be_generated(@ForAll JqwikRandom random) {
			int maxSize = 50;
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers();
			ListArbitrary<Integer> listArbitrary = integerArbitrary.list().ofMaxSize(maxSize);

			RandomGenerator<List<Integer>> generator = listArbitrary.generator(1000, false);

			for (int i = 0; i < 5000; i++) {
				List<Integer> list = generator.next(random).value();
				Statistics.collect(list.size());
			}

			Statistics.coverage(checker -> {
				for (int size = 0; size <= maxSize; size++) {
					checker.check(size).count(c -> c >= 1);
				}
			});
		}

		@Example
		void without_explicit_size_distribution_max_size_should_be_generated_regularly(@ForAll JqwikRandom random) {
			int maxSize = 1000;
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers();
			ListArbitrary<Integer> listArbitrary = integerArbitrary.list().ofMaxSize(maxSize);

			RandomGenerator<List<Integer>> generator = listArbitrary.generator(1000, false);

			for (int i = 0; i < 5000; i++) {
				List<Integer> list = generator.next(random).value();
				Statistics.collect(list.size());
			}

			Statistics.coverage(checker -> {
				// With genSize 1000, calculated probability for size = maxSize is 1%
				checker.check(maxSize).percentage(p -> p >= 0.5);
			});
		}

	}


	private boolean isUniqueModulo(List<Integer> list, int modulo) {
		List<Integer> moduloList = list.stream().map(i -> {
			if (i == null) {
				return null;
			}
			return i % modulo;
		}).collect(Collectors.toList());
		return new LinkedHashSet<>(moduloList).size() == list.size();
	}

	@Example
	void mapEach(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		Arbitrary<List<Tuple.Tuple2<Integer, List<Integer>>>> setArbitrary =
			integerArbitrary
				.list().ofSize(5)
				.mapEach((all, each) -> Tuple.of(each, all));

		RandomGenerator<List<Tuple.Tuple2<Integer, List<Integer>>>> generator = setArbitrary.generator(1, true);

		assertAllGenerated(generator, random, set -> {
			assertThat(set).hasSize(5);
			assertThat(set).allMatch(tuple -> tuple.get2().size() == 5);
		});
	}

	/**
	 * Motivated by performance issues when generating large lists.
	 * See https://github.com/jlink/jqwik/pull/227
	 */
	@Group
	class LargeLists {

		int largeSize = 5000;

		class IntegerAbs {
			public final int value;
			public IntegerAbs(int value) {
				this.value = value;
			}
			@Override public int hashCode() {
				return Integer.hashCode( Math.abs(value) );
			}
			@Override public boolean equals( Object o ) {
				return o instanceof IntegerAbs && Math.abs(value) == Math.abs(((IntegerAbs) o).value);
			}
		}

		@Example
		void plain(@ForAll JqwikRandom random) {
			Arbitrary<List<Integer>> listOfBytes = Arbitraries.integers().list().ofSize(largeSize);
			checkAllGenerated(listOfBytes.generator(1000), random, bytes -> bytes.size() == largeSize);
		}

		@Example
		void unique(@ForAll JqwikRandom random) {
			Arbitrary<List<Integer>> uniqueIntegers = Arbitraries.integers().list().ofSize(largeSize).uniqueElements();
			RandomGenerator<List<Integer>> generator = uniqueIntegers.generator(1000);
			List<Integer> list = generator.next(random).value();
			assertThat(list).hasSize(largeSize);
			assertThat(list).hasSize((int) list.stream().distinct().count());
		}

		@Example
		void uniqueBy(@ForAll JqwikRandom random) {
			Function<IntegerAbs,Object> keyExtractor = x -> x.value;
			Arbitrary<List<IntegerAbs>> uniqueBytes = Arbitraries.integers().map(IntegerAbs::new).list().ofSize(largeSize).uniqueElements(keyExtractor);
			RandomGenerator<List<IntegerAbs>> generator = uniqueBytes.generator(1000);
			List<IntegerAbs> list = generator.next(random).value();
			assertThat(list).hasSize(largeSize);

			// make sure that items are unique by keyExtractor
			long distinctSize = list.stream().map(keyExtractor).distinct().count();
			assertThat(distinctSize).isEqualTo(largeSize);

			// make sure that items are not unique by ByteAbs::hashCode and ByteAbs::equals
			TestingSupport.checkAtLeastOneGenerated(generator, random, ints -> ints.size() > ints.stream().distinct().count());
		}

	}

	@Example
	void flatMapEach(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		Arbitrary<List<Tuple.Tuple2<Integer, Integer>>> setArbitrary =
			integerArbitrary
				.list().ofSize(5)
				.flatMapEach((all, each) ->
								 Arbitraries.of(all).map(friend -> Tuple.of(each, friend))
				);

		RandomGenerator<List<Tuple.Tuple2<Integer, Integer>>> generator = setArbitrary.generator(1, true);

		assertAllGenerated(generator, random, set -> {
			assertThat(set).hasSize(5);
			assertThat(set).allMatch(tuple -> tuple.get2() <= 10);
		});
	}


	@Group
	class ExhaustiveGeneration {

		@Example
		void listsAreCombinationsOfElementsUpToMaxLength() {
			Optional<ExhaustiveGenerator<List<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 3).list().ofMaxSize(2).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<List<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(13);
			assertThat(generator).containsExactly(
				asList(),
				asList(1),
				asList(2),
				asList(3),
				asList(1, 1),
				asList(1, 2),
				asList(1, 3),
				asList(2, 1),
				asList(2, 2),
				asList(2, 3),
				asList(3, 1),
				asList(3, 2),
				asList(3, 3)
			);
		}

		@Example
		void combinationsAreFilteredByUniquenessConstraints() {
			Optional<ExhaustiveGenerator<List<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 3).list().ofMaxSize(2).uniqueElements(i -> i).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<List<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(13);
			assertThat(generator).containsExactlyInAnyOrder(
				asList(),
				asList(1),
				asList(2),
				asList(3),
				asList(1, 2),
				asList(1, 3),
				asList(2, 1),
				asList(2, 3),
				asList(3, 1),
				asList(3, 2)
			);
		}

		@Example
		void elementArbitraryNotExhaustive() {
			Optional<ExhaustiveGenerator<List<Double>>> optionalGenerator =
				Arbitraries.doubles().between(1, 10).list().ofMaxSize(1).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

		@Example
		void tooManyCombinations() {
			Optional<ExhaustiveGenerator<List<Integer>>> optionalGenerator =
				Arbitraries.integers().between(1, 10).list().ofMaxSize(10).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}
	}

	@Group
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list();
			return Arbitraries.of(arbitrary);
		}
	}


	@Group
	class EdgeCasesGeneration implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list();
			return Arbitraries.of(arbitrary);
		}

		@Example
		void edgeCases() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list();
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				Collections.emptyList(),
				Collections.singletonList(-10),
				Collections.singletonList(10)
			);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).hasSize(3);
		}

		@Example
		void edgeCasesWhenMinSize1() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list().ofMinSize(1);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				Collections.singletonList(-10),
				Collections.singletonList(-9),
				Collections.singletonList(-2),
				Collections.singletonList(-1),
				Collections.singletonList(0),
				Collections.singletonList(1),
				Collections.singletonList(2),
				Collections.singletonList(9),
				Collections.singletonList(10)
			);
		}

		@Example
		void edgeCasesWhenMinSizeGreaterThan1() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list().ofMinSize(2);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).isEmpty();
		}

		@Example
		void edgeCasesWhenFixedSize() {
			Arbitrary<Integer> ints = Arbitraries.of(10, 100);
			Arbitrary<List<Integer>> arbitrary = ints.list().ofSize(3);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				asList(10, 10, 10),
				asList(100, 100, 100)
			);
		}

		@Example
		void edgeCasesAreGeneratedFreshlyOnEachCallToIterator() {
			IntegerArbitrary ints = Arbitraries.integers().between(-1, 1);
			Arbitrary<List<Integer>> arbitrary = ints.list();
			EdgeCases<List<Integer>> edgeCases = arbitrary.edgeCases();

			for (Shrinkable<List<Integer>> listShrinkable : edgeCases) {
				listShrinkable.value().add(42);
			}

			Set<List<Integer>> values = collectEdgeCaseValues(edgeCases);
			assertThat(values).containsExactlyInAnyOrder(
				Collections.emptyList(),
				Collections.singletonList(-1),
				Collections.singletonList(0),
				Collections.singletonList(1)
			);
		}

		@Example
		void edgeCasesAreFilteredByUniquenessConstraints() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list().ofSize(2).uniqueElements(i -> i);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).isEmpty();
		}

	}

	@Group
	@PropertyDefaults(tries = 100)
	class Shrinking {

		@Property
		void shrinksToEmptyListByDefault(@ForAll JqwikRandom random) {
			ListArbitrary<Integer> lists = Arbitraries.integers().between(1, 10).list();
			List<Integer> value = falsifyThenShrink(lists, random);
			assertThat(value).isEmpty();
		}

		@Property
		void shrinkToMinSize(@ForAll JqwikRandom random, @ForAll @IntRange(min = 1, max = 20) int min) {
			ListArbitrary<Integer> lists = Arbitraries.integers().between(1, 10).list().ofMinSize(min);
			List<Integer> value = falsifyThenShrink(lists, random);
			assertThat(value).hasSize(min);
			assertThat(value).containsOnly(1);
		}

		@Property
		void shrinkWithUniqueness(@ForAll JqwikRandom random, @ForAll @IntRange(min = 2, max = 10) int min) {
			ListArbitrary<Integer> lists =
				Arbitraries.integers().between(1, 100).list().ofMinSize(min).ofMaxSize(10)
						   .uniqueElements(i -> i);
			List<Integer> value = falsifyThenShrink(lists, random);
			assertThat(value).hasSize(min);
			assertThat(isUniqueModulo(value, 100))
				.describedAs("%s is not unique mod 100", value)
				.isTrue();
			assertThat(value).allMatch(i -> i <= min);
		}

		@Property
		void shrinkWithUniquenessAndNulls(@ForAll JqwikRandom random) {
			ListArbitrary<Integer> lists =
				Arbitraries.integers().between(1, 100).injectNull(0.5)
						   .list().ofMinSize(3).ofMaxSize(10)
						   .uniqueElements(i -> i);
			List<Integer> value = falsifyThenShrink(lists, random);
			assertThat(value).containsExactly(null, 1, 2);
		}

	}

	@Group
	@PropertyDefaults(tries = 100)
	class InvalidValues {

		@Property
		void minSizeOutOfRange(@ForAll @Negative int minSize) {
			assertThatThrownBy(
				() -> Arbitraries.strings().list().ofMinSize(minSize)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void maxSizeOutOfRange(@ForAll @Negative int maxSize) {
			assertThatThrownBy(
				() -> Arbitraries.strings().list().ofMaxSize(maxSize)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void minLargerThanMax(@ForAll @IntRange(min = 0) int minSize, @IntRange(min = 1) @ForAll int maxSize) {
			Assume.that(maxSize < minSize);
			assertThatThrownBy(
				() -> Arbitraries.strings().list().ofMinSize(minSize).ofMaxSize(maxSize)
			).isInstanceOf(IllegalArgumentException.class);
		}
	}

	private void assertGeneratedLists(RandomGenerator<List<String>> generator, int minSize, int maxSize) {
		JqwikRandom random = SourceOfRandomness.current();
		assertAllGenerated(generator, random, list -> {
			assertThat(list.size()).isBetween(minSize, maxSize);
			assertThat(list).isSubsetOf("1", "hallo", "test");
		});
	}

}
