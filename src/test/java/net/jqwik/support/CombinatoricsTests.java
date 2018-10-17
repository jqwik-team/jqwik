package net.jqwik.support;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
@Label("Combinatorics")
class CombinatoricsTests {

	@Group
	@Label("listCombinations")
	class CombineList {

		@Example
		void emptyListOnly() {
			Iterable<Integer> elementIterable = asList(1, 2, 3);
			Iterator<List<Integer>> iterator = Combinatorics.listCombinations(elementIterable, 0, 0);

			assertThat(iterator).containsExactly(
				asList()
			);
		}

		@Example
		void listOfSize1() {
			Iterable<Integer> elementIterable = asList(1, 2, 3);
			Iterator<List<Integer>> iterator = Combinatorics.listCombinations(elementIterable, 1, 1);

			assertThat(iterator).containsExactly(
				asList(1),
				asList(2),
				asList(3)
			);
		}

		@Example
		void listOfSize3() {
			Iterable<Integer> elementIterable = asList(1, 2);
			Iterator<List<Integer>> iterator = Combinatorics.listCombinations(elementIterable, 3, 3);

			assertThat(iterator).containsExactly(
				asList(1, 1, 1),
				asList(1, 1, 2),
				asList(1, 2, 1),
				asList(1, 2, 2),
				asList(2, 1, 1),
				asList(2, 1, 2),
				asList(2, 2, 1),
				asList(2, 2, 2)
			);
		}

		@Example
		void listOfSizeUpTo1() {
			Iterable<Integer> elementIterable = asList(1, 2, 3);
			Iterator<List<Integer>> iterator = Combinatorics.listCombinations(elementIterable, 0, 1);

			assertThat(iterator).containsExactly(
				asList(),
				asList(1),
				asList(2),
				asList(3)
			);
		}

		@Example
		void listOfSize2to3() {
			Iterable<Integer> elementIterable = asList(1, 2);
			Iterator<List<Integer>> iterator = Combinatorics.listCombinations(elementIterable, 2, 3);

			assertThat(iterator).containsExactly(
				asList(1, 1),
				asList(1, 2),
				asList(2, 1),
				asList(2, 2),
				asList(1, 1, 1),
				asList(1, 1, 2),
				asList(1, 2, 1),
				asList(1, 2, 2),
				asList(2, 1, 1),
				asList(2, 1, 2),
				asList(2, 2, 1),
				asList(2, 2, 2)
			);
		}
	}

	@Group
	@Label("setCombinations")
	class CombineSet {

		@Example
		void emptySetOnly() {
			Iterable<Integer> elementIterable = asList(1, 2, 3);
			Iterator<Set<Integer>> iterator = Combinatorics.setCombinations(elementIterable, 0, 0);

			assertThat(iterator).containsExactly(
				asSet()
			);
		}

		private Set<Integer> asSet(Integer... integers) {
			return new HashSet<>(asList(integers));
		}

		@Example
		void setOfSize1() {
			Iterable<Integer> elementIterable = asList(1, 2, 3);
			Iterator<Set<Integer>> iterator = Combinatorics.setCombinations(elementIterable, 1, 1);

			assertThat(iterator).containsExactly(
				asSet(1),
				asSet(2),
				asSet(3)
			);
		}

		@Example
		void setOfSize3() {
			Iterable<Integer> elementIterable = asList(1, 2, 3, 4, 1, 1);
			Iterator<Set<Integer>> iterator = Combinatorics.setCombinations(elementIterable, 3, 3);

			assertThat(iterator).containsExactly(
				asSet(1, 2, 3),
				asSet(1, 2, 4),
				asSet(1, 3, 4),
				asSet(2, 3, 4)
			);
		}

		@Example
		void elementsCannotFillSet() {
			Iterable<Integer> elementIterable = asList(1, 2);
			Iterator<Set<Integer>> iterator = Combinatorics.setCombinations(elementIterable, 3, 3);

			assertThat(iterator).isEmpty();
		}

		@Example
		void setOfSizeUpTo1() {
			Iterable<Integer> elementIterable = asList(1, 2, 3);
			Iterator<Set<Integer>> iterator = Combinatorics.setCombinations(elementIterable, 0, 1);

			assertThat(iterator).containsExactly(
				asSet(),
				asSet(1),
				asSet(2),
				asSet(3)
			);
		}

		@Example
		void setOfSize2to3() {
			Iterable<Integer> elementIterable = asList(1, 2, 3);
			Iterator<Set<Integer>> iterator = Combinatorics.setCombinations(elementIterable, 2, 3);

			assertThat(iterator).containsExactly(
				asSet(1, 2),
				asSet(1, 3),
				asSet(2, 3),
				asSet(1, 2, 3)
			);
		}
	}

	@Group
	@Label("combine")
	class Combine {
		@Example
		void combineNoIterables() {
			List<Iterable<Integer>> iterables = asList();
			Iterator<List<Integer>> iterator = Combinatorics.combine(iterables);

			assertThat(iterator).containsExactly(
				asList()
			);
		}

		@Example
		void combineSingleIterable() {
			List<Iterable<Integer>> iterables = asList(
				asList(1, 2, 3)
			);
			Iterator<List<Integer>> iterator = Combinatorics.combine(iterables);

			assertThat(iterator).containsExactly(
				asList(1), asList(2), asList(3)
			);
		}

		@Example
		void failsWithNoSuchElementException() {
			List<Iterable<Integer>> iterables = asList(
				asList(1)
			);
			Iterator<List<Integer>> iterator = Combinatorics.combine(iterables);
			iterator.next();

			assertThat(iterator.hasNext()).isFalse();
			assertThatThrownBy(() -> iterator.next()).isInstanceOf(NoSuchElementException.class);
		}

		@Example
		void combineTwoIterables() {
			List<Iterable<Object>> iterables = asList(
				asList(1, 2, 3),
				asList('a', 'b')
			);
			Iterator<List<Object>> iterator = Combinatorics.combine(iterables);

			assertThat(iterator).containsExactly(
				asList(1, 'a'), asList(1, 'b'),
				asList(2, 'a'), asList(2, 'b'),
				asList(3, 'a'), asList(3, 'b')
			);
		}

		@Example
		void twoIterablesFirstIsSingleton() {
			List<Iterable<Object>> iterables = asList(
				asList(1),
				asList('a', 'b')
			);
			Iterator<List<Object>> iterator = Combinatorics.combine(iterables);

			assertThat(iterator).containsExactly(
				asList(1, 'a'), asList(1, 'b')
			);
		}

		@Example
		void twoIterablesSecondIsSingleton() {
			List<Iterable<Object>> iterables = asList(
				asList(1, 2, 3),
				asList('a')
			);
			Iterator<List<Object>> iterator = Combinatorics.combine(iterables);

			assertThat(iterator).containsExactly(
				asList(1, 'a'), asList(2, 'a'), asList(3, 'a')
			);
		}

		@Example
		void twoIterablesFirstIsEmpty() {
			List<Iterable<Object>> iterables = asList(
				Collections.emptyList(),
				asList(1, 2, 3)
			);
			Iterator<List<Object>> iterator = Combinatorics.combine(iterables);
			assertThat(iterator.hasNext()).isFalse();
			assertThat(iterator).isEmpty();
		}

		@Example
		void twoIterablesSecondIsEmpty() {
			List<Iterable<Object>> iterables = asList(
				asList(1, 2, 3),
				Collections.emptyList()
			);
			Iterator<List<Object>> iterator = Combinatorics.combine(iterables);
			assertThat(iterator.hasNext()).isFalse();
			assertThat(iterator).isEmpty();
		}

		@Property
		@Label("all combinations produce product of individual iterators")
		void productOfIterables(@ForAll("iterables") @Size(min = 0, max = 4) List<List<Integer>> listOfLists) {

			int product =
				listOfLists
					.stream()
					.mapToInt(List::size)
					.reduce(((left, right) -> left * right)).orElse(1);

			List<Iterable<Integer>> iterables = listOfLists
				.stream()
				.map(aList -> (Iterable<Integer>) aList)
				.collect(Collectors.toList());

			Iterator<List<Integer>> iterator = Combinatorics.combine(iterables);
			assertThat(iterator).hasSize(product);
		}

		@Provide
		Arbitrary<List<List<Integer>>> iterables() {
			return Arbitraries
				.integers().between(-1000, 1000).unique()
				.list().ofMaxSize(15)
				.list();
		}
	}
}
