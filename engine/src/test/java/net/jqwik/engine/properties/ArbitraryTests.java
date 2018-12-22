package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.GenerationMode.*;

@Group
class ArbitraryTests {

	private Random random = SourceOfRandomness.current();

	@Example
	void fixGenSize() {
		int[] injectedGenSize = {0};

		Arbitrary<Integer> arbitrary = genSize -> {
			injectedGenSize[0] = genSize;
			return ignore -> Shrinkable.unshrinkable(0);
		};

		RandomGenerator<Integer> notUsed = arbitrary.fixGenSize(42).generator(1000);
		assertThat(injectedGenSize[0]).isEqualTo(42);
	}


	@Group
	class Generating {
		@Example
		void generateInteger() {
			Arbitrary<Integer> arbitrary = Arbitraries.samples(1, 2, 3, 4, 5);
			RandomGenerator<Integer> generator = arbitrary.generator(10);

			assertThat(generator.next(random).value()).isEqualTo(1);
			assertThat(generator.next(random).value()).isEqualTo(2);
			assertThat(generator.next(random).value()).isEqualTo(3);
			assertThat(generator.next(random).value()).isEqualTo(4);
			assertThat(generator.next(random).value()).isEqualTo(5);
			assertThat(generator.next(random).value()).isEqualTo(1);
		}

		@Example
		void samplesArePrependedToGeneration() {
			Arbitrary<Integer> arbitrary = Arbitraries.samples(1, 2);
			Arbitrary<Integer> arbitraryWithSamples = arbitrary.withSamples(-1, -2);
			RandomGenerator<Integer> generator = arbitraryWithSamples.generator(10);

			assertThat(generator.next(random).value()).isEqualTo(-1);
			assertThat(generator.next(random).value()).isEqualTo(-2);
			assertThat(generator.next(random).value()).isEqualTo(1);
			assertThat(generator.next(random).value()).isEqualTo(2);
			assertThat(generator.next(random).value()).isEqualTo(1);
		}

	}

	@Group
	class Filtering {
		@Example
		void filterInteger() {
			Arbitrary<Integer> arbitrary = Arbitraries.samples(1, 2, 3, 4, 5);
			Arbitrary<Integer> filtered = arbitrary.filter(anInt -> anInt % 2 != 0);
			RandomGenerator<Integer> generator = filtered.generator(10);

			assertThat(generator.next(random).value()).isEqualTo(1);
			assertThat(generator.next(random).value()).isEqualTo(3);
			assertThat(generator.next(random).value()).isEqualTo(5);
			assertThat(generator.next(random).value()).isEqualTo(1);
		}

		@Example
		void failIfFilterWillDiscard10000ValuesInARow() {
			Arbitrary<Integer> arbitrary = Arbitraries.samples(1, 2, 3, 4, 5);
			Arbitrary<Integer> filtered = arbitrary.filter(anInt -> false);
			RandomGenerator<Integer> generator = filtered.generator(10);

			assertThatThrownBy(() -> generator.next(random).value()).isInstanceOf(JqwikException.class);
		}
	}

	@Group
	class Unique {
		@Example
		void uniqueInteger() {
			Arbitrary<Integer> arbitrary = Arbitraries.integers().between(1, 5);
			Arbitrary<Integer> unique = arbitrary.unique();
			RandomGenerator<Integer> generator = unique.generator(10);

			Set<Integer> generatedValues =
				generator.stream(random)
						 .map(Shrinkable::value)
						 .limit(5)
						 .collect(Collectors.toSet());

			assertThat(generatedValues).containsExactly(1, 2, 3, 4, 5);
		}

		@Property
		void uniquenessAcrossGeneratorsMustBeEnforced(@ForAll Random rand) {
			Arbitrary<Integer> primes = Arbitraries.of(2, 3, 5, 7, 11, 13, 17, 19);
			Arbitrary<List<Integer>> uniquePrimes = primes.unique().list().ofSize(2);
			Arbitrary<Integer> product = uniquePrimes.map(p -> p.get(0) * p.get(1));
			RandomGenerator<Integer> generator = product.generator(10);

			Set<Integer> generatedValues =
				generator.stream(rand)
						 .map(Shrinkable::value)
						 .limit(4)
						 .collect(Collectors.toSet());

			assertThat(generatedValues).hasSize(4);
		}

		@Example
		void failIfUniqueWillDiscard10000ValuesInARow() {
			Arbitrary<Integer> arbitrary = Arbitraries.integers().between(1, 3);
			Arbitrary<Integer> unique = arbitrary.unique();
			RandomGenerator<Integer> generator = unique.generator(10);

			generator.next(random);
			generator.next(random);
			generator.next(random);

			assertThatThrownBy(() -> generator.next(random)).isInstanceOf(JqwikException.class);
		}

		@Property(generation = RANDOMIZED)
		void uniquenessIsResetPerTry(@ForAll("uniqueIntegers") int anInt) {
		}

		@Property(generation = RANDOMIZED)
		void uniquenessIsResetForEmbeddedArbitraries(@ForAll("listOfUniqueIntegers") List<Integer> aList) {
			assertThat(aList).hasSize(new HashSet<>(aList).size());
		}

		@Provide
		Arbitrary<Integer> uniqueIntegers() {
			return Arbitraries.integers().between(1, 10).unique();
		}

		@Provide
		Arbitrary<List<Integer>> listOfUniqueIntegers() {
			return uniqueIntegers().list().ofSize(3);
		}

		@Property(generation = RANDOMIZED)
			// There's a small chance that this test fails if 10000 tries won't pick the missing value out of 500 possibilities
		void listOfAllUniqueValuesCanBeGeneratedRandomly(@ForAll("listOfUniqueIntegerPairs") List<Tuple3<Integer, Integer, Integer>> aList) {
			assertThat(aList).hasSize(500);
			assertThat(new HashSet<>(aList)).hasSize(500);
		}

		@Provide
		Arbitrary<List<Tuple3<Integer, Integer, Integer>>> listOfUniqueIntegerPairs() {
			IntegerArbitrary first = Arbitraries.integers().between(1, 10);
			IntegerArbitrary second = Arbitraries.integers().between(1, 10);
			IntegerArbitrary third = Arbitraries.integers().between(1, 5);
			return Combinators.combine(first, second, third).as((f, s, t) -> Tuple.of(f, s, t))
							  .unique().list().ofSize(500);
		}
	}

	@Group
	class StreamOfAllValues {

		@Example
		void generateAllValues() {
			Arbitrary<Integer> arbitrary = Arbitraries.samples(1, 2, 3, 4, 5);
			assertThat(arbitrary.allValues()).isPresent();
			assertThat(arbitrary.allValues().get()).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
		}

		@Example
		void notPossibleWithoutExhaustiveGenerator() {
			Arbitrary<String> arbitrary = Arbitraries.strings();
			assertThat(arbitrary.allValues()).isEmpty();
		}
	}

	@Group
	class Mapping {

		@Example
		void mapIntegerToString() {
			Arbitrary<Integer> arbitrary = Arbitraries.samples(1, 2, 3, 4, 5);
			Arbitrary<String> mapped = arbitrary.map(anInt -> "value=" + anInt);
			RandomGenerator<String> generator = mapped.generator(10);

			assertThat(generator.next(random).value()).isEqualTo("value=1");
			assertThat(generator.next(random).value()).isEqualTo("value=2");
			assertThat(generator.next(random).value()).isEqualTo("value=3");
			assertThat(generator.next(random).value()).isEqualTo("value=4");
			assertThat(generator.next(random).value()).isEqualTo("value=5");
			assertThat(generator.next(random).value()).isEqualTo("value=1");
		}

	}

	@Group
	class FlatMapping {

		@Example
		void flatMapIntegerToString() {
			Arbitrary<Integer> arbitrary = Arbitraries.samples(1, 2, 3, 4, 5);
			Arbitrary<String> mapped = arbitrary.flatMap(anInt -> Arbitraries.strings() //
					.withCharRange('a', 'e') //
					.ofMinLength(anInt).ofMaxLength(anInt));

			RandomGenerator<String> generator = mapped.generator(10);

			assertThat(generator.next(random).value()).hasSize(1);
			assertThat(generator.next(random).value()).hasSize(2);
			assertThat(generator.next(random).value()).hasSize(3);
			assertThat(generator.next(random).value()).hasSize(4);
			assertThat(generator.next(random).value()).hasSize(5);

			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, s -> s.startsWith("a"));
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, s -> s.startsWith("b"));
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, s -> s.startsWith("c"));
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, s -> s.startsWith("d"));
			ArbitraryTestHelper.assertAtLeastOneGenerated(generator, s -> s.startsWith("e"));
		}

	}

	@Group
	class Combination {

		@Example
		void generateCombination() {
			Arbitrary<Integer> a1 = Arbitraries.samples(1, 2, 3);
			Arbitrary<Integer> a2 = Arbitraries.samples(4, 5, 6);
			Arbitrary<String> combined = Combinators.combine(a1, a2).as((i1, i2) -> i1 + ":" + i2);
			RandomGenerator<String> generator = combined.generator(10);

			assertThat(generator.next(random).value()).isEqualTo("1:4");
			assertThat(generator.next(random).value()).isEqualTo("2:5");
			assertThat(generator.next(random).value()).isEqualTo("3:6");
			assertThat(generator.next(random).value()).isEqualTo("1:4");
		}

		@Example
		void shrinkCombination() {
			Arbitrary<Integer> a1 = Arbitraries.samples(1, 2, 3);
			Arbitrary<Integer> a2 = Arbitraries.samples(4, 5, 6);
			Arbitrary<String> combined = Combinators.combine(a1, a2).as((i1, i2) -> i1 + ":" + i2);
			RandomGenerator<String> generator = combined.generator(10);

			Shrinkable<String> value3to6 = generateNth(generator, 3);
			assertThat(value3to6.value()).isEqualTo("3:6");

			ShrinkingSequence<String> sequence = value3to6.shrink(ignore -> false);
			while(sequence.next(() -> {}, ignore -> {}));
			assertThat(sequence.current().value()).isEqualTo("1:4");
		}

	}

	private <T> Shrinkable<T> generateNth(RandomGenerator<T> generator, int n) {
		Shrinkable<T> generated = null;
		for (int i = 0; i < n; i++) {
			generated = generator.next(random);
		}
		return generated;
	}

}
