package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.edgeCases.*;
import net.jqwik.api.statistics.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class ArrayArbitraryTests {

	@Example
	void array(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		ArrayArbitrary<Integer, Integer[]> arrayArbitrary = integerArbitrary.array(Integer[].class).ofMinSize(2).ofMaxSize(5);

		RandomGenerator<Integer[]> generator = arrayArbitrary.generator(1, true);

		assertAllGenerated(generator, random, array -> {
			assertThat(array.length).isBetween(2, 5);
			assertThat(array).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		});
	}

	@Example
	void arrayOfSupertype(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		ArrayArbitrary<Integer, Object[]> arrayArbitrary = integerArbitrary.array(Object[].class).ofMinSize(2).ofMaxSize(5);

		RandomGenerator<Object[]> generator = arrayArbitrary.generator(1, true);

		assertAllGenerated(generator, random, array -> {
			assertThat(array.length).isBetween(2, 5);
			assertThat(array).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		});
	}

	@Example
	void notAnArrayType() {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		assertThatThrownBy(() -> integerArbitrary.array(String.class)).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void arrayForComponentClass(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		ArrayArbitrary<Integer, Integer[]> arrayArbitrary =
			DefaultArrayArbitrary.forComponentType(integerArbitrary, Integer.class).ofMinSize(2).ofMaxSize(5);

		RandomGenerator<Integer[]> generator = arrayArbitrary.generator(1, true);

		assertAllGenerated(generator, random, array -> {
			assertThat(array.length).isBetween(2, 5);
			assertThat(array).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		});
	}

	@Example
	@StatisticsReport(onFailureOnly = true)
	void withSizeDistribution(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers();
		ArrayArbitrary<Integer, Integer[]> arbitrary =
			integerArbitrary.array(Integer[].class).ofMaxSize(100)
							.withSizeDistribution(RandomDistribution.uniform());

		RandomGenerator<Integer[]> generator = arbitrary.generator(1, false);

		for (int i = 0; i < 5000; i++) {
			Integer[] array = generator.next(random).value();
			Statistics.collect(array.length);
		}

		Statistics.coverage(checker -> {
			for (int size = 0; size <= 100; size++) {
				checker.check(size).percentage(p -> p >= 0.4);
			}
		});
	}

	@Example
	void reduceArray(@ForAll JqwikRandom random) {
		ArrayArbitrary<Integer, Integer[]> arrayArbitrary =
			Arbitraries.integers().between(1, 5).array(Integer[].class).ofMinSize(1).ofMaxSize(10);

		Arbitrary<Integer> integerArbitrary = arrayArbitrary.reduce(0, Integer::sum);

		RandomGenerator<Integer> generator = integerArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, sum -> {
			assertThat(sum).isBetween(1, 50);
		});

		TestingSupport.checkAtLeastOneGenerated(generator, random, sum -> sum == 1);
		TestingSupport.checkAtLeastOneGenerated(generator, random, sum -> sum > 30);
	}

	@Example
	void arrayOfPrimitiveType(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		ArrayArbitrary<Integer, int[]> arrayArbitrary = integerArbitrary.array(int[].class).ofMinSize(0).ofMaxSize(5);

		RandomGenerator<int[]> generator = arrayArbitrary.generator(1, true);

		Shrinkable<int[]> array = generator.next(random);
		assertThat(array.value().length).isBetween(0, 5);
		List<Integer> actual = IntStream.of(array.value()).boxed().collect(Collectors.toList());
		assertThat(actual).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	@Example
	void uniquenessConstraint(@ForAll JqwikRandom random) {
		ArrayArbitrary<Integer, Integer[]> listArbitrary =
			Arbitraries.integers().between(1, 1000).array(Integer[].class).ofMaxSize(20)
					   .uniqueElements(i -> i % 100);

		RandomGenerator<Integer[]> generator = listArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, array -> {
			assertThat(isUniqueModulo(array, 100)).isTrue();
		});
	}

	@Example
	void uniqueElements(@ForAll JqwikRandom random) {
		ArrayArbitrary<Integer, Integer[]> listArbitrary =
			Arbitraries.integers().between(1, 1000).array(Integer[].class).ofMaxSize(20)
					   .uniqueElements();

		RandomGenerator<Integer[]> generator = listArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, array -> {
			assertThat(isUniqueModulo(array, 1000)).isTrue();
		});
	}

	@Group
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			ArrayArbitrary<Integer, Integer[]> arbitrary = ints.array(Integer[].class);
			return Arbitraries.of(arbitrary);
		}
	}

	@Group
	class EdgeCasesGeneration implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			ArrayArbitrary<Integer, Integer[]> arbitrary = ints.array(Integer[].class);
			return Arbitraries.of(arbitrary);
		}

		@Example
		void edgeCases() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			ArrayArbitrary<Integer, Integer[]> arbitrary = ints.array(Integer[].class);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				new Integer[]{},
				new Integer[]{-10},
				new Integer[]{10}
			);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).hasSize(3);
		}

		@Example
		void edgeCasesAreFilteredByUniquenessConstraints() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			ArrayArbitrary<Integer, Integer[]> arbitrary = ints.array(Integer[].class).ofSize(2).uniqueElements(i -> i);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).isEmpty();
		}
	}

	private boolean isUniqueModulo(Integer[] array, int modulo) {
		List<Integer> list = Arrays.asList(array);
		List<Integer> modulo100 = list.stream().map(i -> {
			if (i == null) {
				return null;
			}
			return i % modulo;
		}).collect(Collectors.toList());
		return new HashSet<>(modulo100).size() == list.size();
	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void arraysAreCombinationsOfElementsUpToMaxLength() {
			Optional<ExhaustiveGenerator<Integer[]>> optionalGenerator =
				Arbitraries.integers().between(1, 2).array(Integer[].class)
						   .ofMaxSize(2).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Integer[]> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(7);
			assertThat(generator).containsExactly(
				new Integer[]{},
				new Integer[]{1},
				new Integer[]{2},
				new Integer[]{1, 1},
				new Integer[]{1, 2},
				new Integer[]{2, 1},
				new Integer[]{2, 2}
			);
		}

		@Example
		void combinationsAreFilteredByUniquenessConstraints() {
			Optional<ExhaustiveGenerator<Integer[]>> optionalGenerator =
				Arbitraries.integers().between(1, 2).array(Integer[].class)
						   .ofMaxSize(2).uniqueElements(i -> i).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Integer[]> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(7);
			assertThat(generator).containsExactly(
				new Integer[]{},
				new Integer[]{1},
				new Integer[]{2},
				new Integer[]{1, 2},
				new Integer[]{2, 1}
			);
		}

		@Example
		void elementArbitraryNotExhaustive() {
			Optional<ExhaustiveGenerator<Double[]>> optionalGenerator =
				Arbitraries.doubles().between(1, 10).array(Double[].class).ofMaxSize(1).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

		@Example
		void tooManyCombinations() {
			Optional<ExhaustiveGenerator<Integer[]>> optionalGenerator =
				Arbitraries.integers().between(1, 10).array(Integer[].class).ofMaxSize(10).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}
	}

	@Group
	@PropertyDefaults(tries = 100)
	class Shrinking {

		@Property
		void shrinksToEmptyArrayByDefault(@ForAll JqwikRandom random) {
			ArrayArbitrary<Integer, Integer[]> arrays = Arbitraries.integers().between(1, 10).array(Integer[].class);
			Integer[] value = falsifyThenShrink(arrays, random);
			assertThat(value).isEmpty();
		}

		@Property
		void shrinkToMinSize(@ForAll JqwikRandom random, @ForAll @IntRange(min = 1, max = 20) int min) {
			ArrayArbitrary<Integer, Integer[]> arrays = Arbitraries.integers().between(1, 10).array(Integer[].class).ofMinSize(min);
			Integer[] value = falsifyThenShrink(arrays, random);
			assertThat(value).hasSize(min);
			assertThat(value).containsOnly(1);
		}

		@Property
		void shrinkWithUniqueness(@ForAll JqwikRandom random, @ForAll @IntRange(min = 2, max = 10) int min) {
			ArrayArbitrary<Integer, Integer[]> lists =
				Arbitraries.integers().between(1, 100).array(Integer[].class).ofMinSize(min).ofMaxSize(10)
						   .uniqueElements(i -> i);
			Integer[] value = falsifyThenShrink(lists, random);
			assertThat(value).hasSize(min);
			assertThat(isUniqueModulo(value, 100))
				.describedAs("%s is not unique mod 100", Arrays.toString(value))
				.isTrue();
			assertThat(value).allMatch(i -> i <= min);
		}

	}

}