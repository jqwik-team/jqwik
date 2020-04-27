package net.jqwik.api;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ArbitraryTestHelper.*;
import static net.jqwik.api.GenerationMode.*;

@Group
@Label("Arbitrary")
class ArbitraryTests {

	@Example
	void fixGenSize() {
		int[] injectedGenSize = {0};

		Arbitrary<Integer> arbitrary =
			new Arbitrary<Integer>() {
				@Override
				public RandomGenerator<Integer> generator(final int genSize) {
					injectedGenSize[0] = genSize;
					return ignore -> Shrinkable.unshrinkable(0);
				}

				@Override
				public EdgeCases<Integer> edgeCases() {
					return EdgeCases.none();
				}
			};

		RandomGenerator<Integer> notUsed = arbitrary.fixGenSize(42).generator(1000);
		assertThat(injectedGenSize[0]).isEqualTo(42);
	}

	@Example
	void generateInteger(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5);
		RandomGenerator<Integer> generator = arbitrary.generator(10);

		assertThat(generator.next(random).value()).isEqualTo(1);
		assertThat(generator.next(random).value()).isEqualTo(2);
		assertThat(generator.next(random).value()).isEqualTo(3);
		assertThat(generator.next(random).value()).isEqualTo(4);
		assertThat(generator.next(random).value()).isEqualTo(5);
		assertThat(generator.next(random).value()).isEqualTo(1);
	}

	@Example
	void nullsWithProbability50Percent() {
		Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
		Arbitrary<Integer> intsWithNulls = ints.injectNull(0.5);

		List<Integer> listWithNulls = intsWithNulls.list()
												   .ofMinSize(99) // Fixed size lists create edge case
												   .ofMaxSize(100).sample();
		listWithNulls.removeIf(Objects::isNull);

		// Might very rarely fail
		assertThat(listWithNulls).hasSizeLessThanOrEqualTo(75);
	}

	@Group
	class Filtering {
		@Example
		void filterInteger(@ForAll Random random) {
			Arbitrary<Integer> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5);
			Arbitrary<Integer> filtered = arbitrary.filter(anInt -> anInt % 2 != 0);
			RandomGenerator<Integer> generator = filtered.generator(10);

			assertThat(generator.next(random).value()).isEqualTo(1);
			assertThat(generator.next(random).value()).isEqualTo(3);
			assertThat(generator.next(random).value()).isEqualTo(5);
			assertThat(generator.next(random).value()).isEqualTo(1);
		}

		@Example
		void failIfFilterWillDiscard10000ValuesInARow(@ForAll Random random) {
			Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4, 5);
			Arbitrary<Integer> filtered = arbitrary.filter(anInt -> false);
			RandomGenerator<Integer> generator = filtered.generator(10);

			assertThatThrownBy(() -> generator.next(random).value()).isInstanceOf(JqwikException.class);
		}
	}

	@Group
	@Label("Unique")
	class UniqueMethod {
		@Example
		void uniqueInteger(@ForAll Random random) {
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
		void failIfUniqueWillDiscard10000ValuesInARow(@ForAll Random random) {
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

		@Property
		void listOfUniqueIntegersWithLessElementsThanMaxSize(@ForAll List<@Unique @IntRange(min = 1, max = 5) Integer> list) {
			assertThat(list).hasSizeLessThanOrEqualTo(5);
		}

		@Property
		void arrayOfUniqueIntegersWithLessElementsThanMaxSize(@ForAll("arrayOfUniqueIntegers") Integer[] array) {
			assertThat(array).hasSizeLessThanOrEqualTo(5);
		}

		@Provide
		Arbitrary<Integer[]> arrayOfUniqueIntegers() {
			return Arbitraries.integers().between(1, 5).unique().array(Integer[].class);
		}

		// There's a small chance that this test fails if 10000 tries won't pick the missing value out of 500 possibilities
		@Property(tries = 100, generation = RANDOMIZED)
		void listOfAllUniqueValuesCanBeGeneratedRandomly(
			@ForAll("listOfUniqueIntegerPairs") List<Tuple3<Integer, Integer, Integer>> aList
		) {
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
			Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4, 5);
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
	class ForEachValue {

		@Example
		void iterateThroughEachValue() {
			Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4, 5);
			AtomicInteger count = new AtomicInteger(0);
			arbitrary.forEachValue(i -> {
				count.incrementAndGet();
				assertThat(i).isIn(1, 2, 3, 4, 5);
			});
			assertThat(count.get()).isEqualTo(5);
		}

		@Example
		void notPossibleWithoutExhaustiveGenerator() {
			Arbitrary<String> arbitrary = Arbitraries.strings();
			Assertions.assertThatThrownBy(() -> arbitrary.forEachValue(i -> {}))
					  .isInstanceOf(AssertionError.class);
		}
	}

	@Group
	class Mapping {

		@Example
		void mapIntegerToString(@ForAll Random random) {
			Arbitrary<Integer> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5);
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
		void flatMapIntegerToString(@ForAll Random random) {
			Arbitrary<Integer> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5);
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
		void generateCombination(@ForAll Random random) {
			Arbitrary<Integer> a1 = new OrderedArbitraryForTesting<>(1, 2, 3);
			Arbitrary<Integer> a2 = new OrderedArbitraryForTesting<>(4, 5, 6);
			Arbitrary<String> combined = Combinators.combine(a1, a2).as((i1, i2) -> i1 + ":" + i2);
			RandomGenerator<String> generator = combined.generator(10);

			assertThat(generator.next(random).value()).isEqualTo("1:4");
			assertThat(generator.next(random).value()).isEqualTo("2:5");
			assertThat(generator.next(random).value()).isEqualTo("3:6");
			assertThat(generator.next(random).value()).isEqualTo("1:4");
		}

		@Example
		void shrinkCombination(@ForAll Random random) {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2, 3);
			Arbitrary<Integer> a2 = Arbitraries.of(4, 5, 6);
			Arbitrary<String> combined = Combinators.combine(a1, a2).as((i1, i2) -> i1 + ":" + i2);
			Shrinkable<String> value3to6 = combined.generator(10).next(random);

			ShrinkingSequence<String> sequence = value3to6.shrink(ignore1 -> TryExecutionResult.falsified(null));
			while (sequence.next(() -> {}, ignore -> {})) ;
			assertThat(sequence.current().value()).isEqualTo("1:4");
		}

	}

	@Group
	class Collect {

		@Example
		void collectList() {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 3);
			Arbitrary<List<Integer>> collected = integers.collect(list -> sum(list) >= 10);
			RandomGenerator<List<Integer>> generator = collected.generator(10);

			assertAllGenerated(generator, value -> {
				assertThat(sum(value)).isBetween(10, 12);
				assertThat(value.size()).isBetween(4, 10);
			});
		}

		@Example
		void collectListWillThrowExceptionIfTooBig(@ForAll Random random) {
			Arbitrary<Integer> integers = Arbitraries.integers().between(1, 3);
			Arbitrary<List<Integer>> collected = integers.collect(list -> sum(list) < 0);
			RandomGenerator<List<Integer>> generator = collected.generator(10);

			assertThatThrownBy(() -> generator.next(random))
				.isInstanceOf(JqwikException.class);
		}

		private int sum(List<Integer> list) {
			return list.stream().mapToInt(i -> i).sum();
		}

	}

	@Group
	class Sampling {

		@Property
		void singleSample(@ForAll @Size(min = 1) List<Integer> values) {
			Arbitrary<Integer> ints = Arbitraries.of(values);

			Integer anInt = ints.sample();

			assertThat(anInt).isIn(values);
		}

		@Property
		void sampleStream(@ForAll @Size(min = 1) List<Integer> values) {
			Arbitrary<Integer> ints = Arbitraries.of(values);

			ints.sampleStream()
				.limit(10)
				.forEach(anInt -> assertThat(anInt).isIn(values));
		}

	}

	@Group
	class Duplicates {

		@Example
		void duplicatesWithProbability20Percent() {
			Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
			Arbitrary<Integer> intsWithDuplicates = ints.injectDuplicates(0.2);

			List<Integer> listWithDuplicates = intsWithDuplicates.list().ofSize(100).sample();
			Set<Integer> noMoreDuplicates = new HashSet<>(listWithDuplicates);

			// Might very rarely fail
			assertThat(noMoreDuplicates).hasSizeLessThanOrEqualTo(90);
		}

		@Example
		void duplicatesWith50Percent() {
			Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
			Arbitrary<Integer> intsWithDuplicates = ints.injectDuplicates(0.5);

			List<Integer> listWithDuplicates = intsWithDuplicates.list().ofSize(100).sample();
			Set<Integer> noMoreDuplicates = new HashSet<>(listWithDuplicates);

			// Might very rarely fail
			assertThat(noMoreDuplicates).hasSizeLessThanOrEqualTo(65);
		}

		@Example
		void duplicatesWith100Percent() {
			Arbitrary<Integer> ints = Arbitraries.integers().between(-1000, 1000);
			Arbitrary<Integer> intsWithDuplicates = ints.injectDuplicates(1.0);

			List<Integer> listWithDuplicates = intsWithDuplicates.list().ofSize(100).sample();
			Set<Integer> noMoreDuplicates = new HashSet<>(listWithDuplicates);

			assertThat(noMoreDuplicates).hasSize(1);
		}
	}

	@Group
	@Label("tuple1..4")
	class TupleOfSameType {

		@Example
		void tuple1() {
			Arbitrary<Integer> integers = Arbitraries.constant(1);
			Arbitrary<Tuple1<Integer>> tuple = integers.tuple1();

			assertThat(tuple.sample()).isEqualTo(Tuple.of(1));
		}

		@Example
		void tuple2() {
			Arbitrary<Integer> integers = Arbitraries.of(1, 2);
			Arbitrary<Tuple2<Integer, Integer>> tuple = integers.tuple2();

			Tuple2<Integer, Integer> sample = tuple.sample();
			assertThat(sample.v1).isIn(1, 2);
			assertThat(sample.v2).isIn(1, 2);
		}

		@Example
		void tuple3() {
			Arbitrary<Integer> integers = Arbitraries.of(1, 2);
			Arbitrary<Tuple3<Integer, Integer, Integer>> tuple = integers.tuple3();

			Tuple3<Integer, Integer, Integer> sample = tuple.sample();
			assertThat(sample.v1).isIn(1, 2);
			assertThat(sample.v2).isIn(1, 2);
			assertThat(sample.v3).isIn(1, 2);
		}

		@Example
		void tuple4() {
			Arbitrary<Integer> integers = Arbitraries.of(1, 2);
			Arbitrary<Tuple4<Integer, Integer, Integer, Integer>> tuple = integers.tuple4();

			Tuple4<Integer, Integer, Integer, Integer> sample = tuple.sample();
			assertThat(sample.v1).isIn(1, 2);
			assertThat(sample.v2).isIn(1, 2);
			assertThat(sample.v3).isIn(1, 2);
			assertThat(sample.v4).isIn(1, 2);
		}

	}

}
