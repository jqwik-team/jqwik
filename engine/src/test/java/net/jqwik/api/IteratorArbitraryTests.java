package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class IteratorArbitraryTests {

	@Example
	void iterators(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		IteratorArbitrary<Integer> streamArbitrary = integerArbitrary.iterator().ofMinSize(0).ofMaxSize(5);

		RandomGenerator<Iterator<Integer>> generator = streamArbitrary.generator(1, true);

		assertGeneratedIterator(generator.next(random));
		assertGeneratedIterator(generator.next(random));
		assertGeneratedIterator(generator.next(random));
		assertGeneratedIterator(generator.next(random));
	}

	@Example
	@StatisticsReport(onFailureOnly = true)
	void withSizeDistribution(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers();
		IteratorArbitrary<Integer> arbitrary =
			integerArbitrary.iterator().ofMaxSize(100)
							.withSizeDistribution(RandomDistribution.uniform());

		RandomGenerator<Iterator<Integer>> generator = arbitrary.generator(1, false);

		for (int i = 0; i < 5000; i++) {
			Iterator<Integer> stream = generator.next(random).value();
			List<Integer> list = toList(stream);
			Statistics.collect(list.size());
		}

		Statistics.coverage(checker -> {
			for (int size = 0; size <= 100; size++) {
				checker.check(size).percentage(p -> p >= 0.4);
			}
		});
	}

	@Example
	void uniquenessConstraint(@ForAll JqwikRandom random) {
		IteratorArbitrary<Integer> listArbitrary =
				Arbitraries.integers().between(1, 1000).iterator().ofMaxSize(20)
						   .uniqueElements(i -> i % 100);

		RandomGenerator<Iterator<Integer>> generator = listArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, iterator -> {
			assertThat(isUniqueModulo(iterator, 100)).isTrue();
		});
	}

	@Example
	void uniquenessElements(@ForAll JqwikRandom random) {
		IteratorArbitrary<Integer> listArbitrary =
				Arbitraries.integers().between(1, 1000).iterator().ofMaxSize(20).uniqueElements();

		RandomGenerator<Iterator<Integer>> generator = listArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, iterator -> {
			assertThat(isUniqueModulo(iterator, 1000)).isTrue();
		});
	}

	@Example
	void edgeCases() {
		Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
		IteratorArbitrary<Integer> arbitrary = ints.iterator();
		Set<Iterator<Integer>> iterators = collectEdgeCaseValues(arbitrary.edgeCases());
		Set<List<Integer>> lists =
				iterators.stream()
						 .map(iterator -> {
							 List<Integer> list = new ArrayList<>();
							 while (iterator.hasNext()) { list.add(iterator.next()); }
							 return list;
						 })
						 .collect(Collectors.toSet());
		assertThat(lists).containsExactlyInAnyOrder(
				Collections.emptyList(),
				Collections.singletonList(-10),
				Collections.singletonList(10)
		);
		assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).hasSize(3);
	}


	@Example
	void edgeCasesAreFilteredByUniquenessConstraints() {
		IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
		IteratorArbitrary<Integer> arbitrary = ints.iterator().ofSize(2).uniqueElements(i -> i);
		assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).isEmpty();
	}

	private boolean isUniqueModulo(Iterator<Integer> iterator, int modulo) {
		Set<Integer> moduloSet = new HashSet<>();
		int count = 0;
		while (iterator.hasNext()) {
			count++;
			Integer i = iterator.next();
			if (i == null) {
				moduloSet.add(null);
			}
			moduloSet.add(i % modulo);
		}
		return moduloSet.size() == count;
	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void iteratorsAreCombinationsOfElementsUpToMaxLength() {
			Optional<ExhaustiveGenerator<Iterator<Integer>>> optionalGenerator =
					Arbitraries.integers().between(1, 3).iterator().ofMaxSize(2).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Iterator<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(13);
			ExhaustiveGenerator<List<Integer>> listGenerator = generator.map(IteratorArbitraryTests.this::toList);
			assertThat(listGenerator).containsExactly(
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
			Optional<ExhaustiveGenerator<Iterator<Integer>>> optionalGenerator =
					Arbitraries.integers().between(1, 3).iterator().ofMaxSize(2).uniqueElements(i -> i).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Iterator<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(13);
			ExhaustiveGenerator<List<Integer>> listGenerator = generator.map(IteratorArbitraryTests.this::toList);
			assertThat(listGenerator).containsExactly(
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
			Optional<ExhaustiveGenerator<Iterator<Double>>> optionalGenerator =
					Arbitraries.doubles().between(1, 10).iterator().ofMaxSize(1).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

		@Example
		void tooManyCombinations() {
			Optional<ExhaustiveGenerator<Iterator<Integer>>> optionalGenerator =
					Arbitraries.integers().between(1, 10).iterator().ofMaxSize(10).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

	}

	@Group
	@PropertyDefaults(tries = 100)
	class Shrinking {

		@Property
		void shrinksToEmptyStreamByDefault(@ForAll JqwikRandom random) {
			IteratorArbitrary<Integer> iterators = Arbitraries.integers().between(1, 10).iterator();
			Iterator<Integer> value = falsifyThenShrink(iterators, random);
			assertThat(value.hasNext()).isFalse();
		}

		@Property
		void shrinkToMinSize(@ForAll JqwikRandom random, @ForAll @IntRange(min = 1, max = 20) int min) {
			IteratorArbitrary<Integer> iterators = Arbitraries.integers().between(1, 10).iterator().ofMinSize(min);
			Iterator<Integer> value = falsifyThenShrink(iterators, random);
			List<Integer> list = toList(value);
			assertThat(list).hasSize(min);
			assertThat(list).containsOnly(1);
		}

	}

	private void assertGeneratedIterator(Shrinkable<Iterator<Integer>> shrinkable) {
		Set<Integer> set = new HashSet<>();
		Iterator<Integer> iterator = shrinkable.value();
		while (iterator.hasNext()) {
			set.add(iterator.next());
		}
		assertThat(set.size()).isBetween(0, 5);
		assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	private <T> List<T> toList(Iterator<T> i) {
		List<T> list = new ArrayList<>();
		while (i.hasNext()) {
			list.add(i.next());
		}
		return list;
	}


}
