package net.jqwik.support;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
class CombinatoricsTests {

	@Example
	void combineNoIterables() {
		List<Iterable> iterables = asList();
		Iterator<List> iterator = Combinatorics.combine(iterables);

		assertThat(iterator).containsExactly(
			asList()
		);
	}

	@Example
	void combineSingleIterable() {
		List<Iterable> iterables = asList(
			asList(1, 2, 3)
		);
		Iterator<List> iterator = Combinatorics.combine(iterables);

		assertThat(iterator).containsExactly(
			asList(1), asList(2), asList(3)
		);
	}

	@Example
	void failsWithNoSuchElementException() {
		List<Iterable> iterables = asList(
			asList(1)
		);
		Iterator<List> iterator = Combinatorics.combine(iterables);
		iterator.next();

		assertThat(iterator.hasNext()).isFalse();
		assertThatThrownBy(() -> iterator.next()).isInstanceOf(NoSuchElementException.class);
	}

	@Example
	void combineTwoIterables() {
		List<Iterable> iterables = asList(
			asList(1, 2, 3),
			asList('a', 'b')
		);
		Iterator<List> iterator = Combinatorics.combine(iterables);

		assertThat(iterator).containsExactly(
			asList(1, 'a'), asList(1, 'b'),
			asList(2, 'a'), asList(2, 'b'),
			asList(3, 'a'), asList(3, 'b')
		);
	}

	@Example
	void twoIterablesFirstIsSingleton() {
		List<Iterable> iterables = asList(
			asList(1),
			asList('a', 'b')
		);
		Iterator<List> iterator = Combinatorics.combine(iterables);

		assertThat(iterator).containsExactly(
			asList(1, 'a'), asList(1, 'b')
		);
	}

	@Example
	void twoIterablesSecondIsSingleton() {
		List<Iterable> iterables = asList(
			asList(1, 2, 3),
			asList('a')
		);
		Iterator<List> iterator = Combinatorics.combine(iterables);

		assertThat(iterator).containsExactly(
			asList(1, 'a'), asList(2, 'a'), asList(3, 'a')
		);
	}

	@Example
	void twoIterablesFirstIsEmpty() {
		List<Iterable> iterables = asList(
			Collections.emptyList(),
			asList(1, 2, 3)
		);
		Iterator<List> iterator = Combinatorics.combine(iterables);
		assertThat(iterator.hasNext()).isFalse();
		assertThat(iterator).isEmpty();
	}

	@Example
	void twoIterablesSecondIsEmpty() {
		List<Iterable> iterables = asList(
			asList(1, 2, 3),
			Collections.emptyList()
		);
		Iterator<List> iterator = Combinatorics.combine(iterables);
		assertThat(iterator.hasNext()).isFalse();
		assertThat(iterator).isEmpty();
	}

	@Property
	@Label("all combinations produce product of individual iterators")
	void productOfIterables(@ForAll("iterables") @Size(min = 0, max = 4) List<List> listOfLists) {

		int product =
			listOfLists
				.stream()
				.mapToInt(List::size)
				.reduce(((left, right) -> left * right)).orElse(1);

		List<Iterable> iterables = listOfLists
									   .stream()
									   .map(aList -> (Iterable) aList)
									   .collect(Collectors.toList());

		Iterator<List> iterator = Combinatorics.combine(iterables);
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
