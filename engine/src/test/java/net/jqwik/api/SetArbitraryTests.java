package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ArbitraryTestHelper.*;
import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class SetArbitraryTests {

	@Example
	void set() {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		SetArbitrary<Integer> setArbitrary = integerArbitrary.set().ofMinSize(2).ofMaxSize(7);

		RandomGenerator<Set<Integer>> generator = setArbitrary.generator(1);

		assertGeneratedSet(generator, 2, 7);
	}

	@Example
	void setWithLessElementsThanMaxSize() {
		Arbitrary<Integer> integerArbitrary = Arbitraries.of(1, 2, 3, 4, 5);
		SetArbitrary<Integer> setArbitrary = integerArbitrary.set().ofMinSize(2);

		RandomGenerator<Set<Integer>> generator = setArbitrary.generator(1);

		assertGeneratedSet(generator, 2, 5);
	}

	@Example
	void mapEach() {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		Arbitrary<Set<Tuple.Tuple2<Integer, Set<Integer>>>> setArbitrary =
				integerArbitrary
						.set().ofSize(5)
						.mapEach((all, each) -> Tuple.of(each, all));

		RandomGenerator<Set<Tuple.Tuple2<Integer, Set<Integer>>>> generator = setArbitrary.generator(1);

		assertAllGenerated(generator, set -> {
			assertThat(set).hasSize(5);
			assertThat(set).allMatch(tuple -> tuple.get2().size() == 5);
		});
	}

	@Example
	void flatMapEach() {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		Arbitrary<Set<Tuple.Tuple2<Integer, Integer>>> setArbitrary =
				integerArbitrary
						.set().ofSize(5)
						.flatMapEach((all, each) ->
											 Arbitraries.of(all).map(friend -> Tuple.of(each, friend))
						);

		RandomGenerator<Set<Tuple.Tuple2<Integer, Integer>>> generator = setArbitrary.generator(1);

		assertAllGenerated(generator, set -> {
			assertThat(set).hasSize(5);
			assertThat(set).allMatch(tuple -> tuple.get2() <= 10);
		});
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
			return new HashSet<>(asList(ints));
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
	class EdgeCasesGeneration {

		@Example
		void setEdgeCases() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set();
			assertThat(collectEdgeCases(arbitrary.edgeCases())).containsExactlyInAnyOrder(
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
			assertThat(collectEdgeCases(arbitrary.edgeCases())).hasSize(10);
		}

		@Example
		void setEdgeCasesWithMinSize1() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set().ofMinSize(1);
			assertThat(collectEdgeCases(arbitrary.edgeCases())).containsExactlyInAnyOrder(
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

	}


	private void assertGeneratedSet(RandomGenerator<Set<Integer>> generator, int minSize, int maxSize) {
		assertAllGenerated(generator, set -> {
			assertThat(set.size()).isBetween(minSize, maxSize);
			assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		});
	}

}