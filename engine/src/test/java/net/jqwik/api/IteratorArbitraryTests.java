package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class IteratorArbitraryTests {

	@Example
	void iterators(@ForAll Random random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		IteratorArbitrary<Integer> streamArbitrary = integerArbitrary.iterator().ofMinSize(0).ofMaxSize(5);

		RandomGenerator<Iterator<Integer>> generator = streamArbitrary.generator(1);

		assertGeneratedIterator(generator.next(random));
		assertGeneratedIterator(generator.next(random));
		assertGeneratedIterator(generator.next(random));
		assertGeneratedIterator(generator.next(random));
	}

	@Example
	void iteratorEdgeCases() {
		Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
		Arbitrary<Iterator<Integer>> arbitrary = ints.iterator();
		Set<Iterator<Integer>> iterators = collectEdgeCases(arbitrary.edgeCases());
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
		assertThat(collectEdgeCases(arbitrary.edgeCases())).hasSize(3);
	}


	@Group
	class ExhaustiveGeneration {

		@Example
		void listsAreCombinationsOfElementsUpToMaxLength() {
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
		void shrinksToEmptyListByDefault(@ForAll Random random) {
			IteratorArbitrary<Integer> lists = Arbitraries.integers().between(1, 10).iterator();
			Iterator<Integer> value = falsifyThenShrink(lists, random);
			assertThat(value.hasNext()).isFalse();
		}

		@Property
		void shrinkToMinSize(@ForAll Random random, @ForAll @IntRange(min = 1, max = 20) int min) {
			IteratorArbitrary<Integer> lists = Arbitraries.integers().between(1, 10).iterator().ofMinSize(min);
			Iterator<Integer> value = falsifyThenShrink(lists, random);
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
