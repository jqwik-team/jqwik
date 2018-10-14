package net.jqwik.support;

import java.util.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

import static java.util.Arrays.asList;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
class CombinatoricsTests {

	@Example
	void combineSingleIterable() {
		List<Iterable> iterables = asList(
			asList(1, 2, 3)
		);
		Iterator<List> iterator = Combinatorics.combine(iterables);

		Assertions.assertThat(iterator).containsExactly(
			asList(1), asList(2), asList(3)
		);
	}

	@Example
	void combineTwoIterables() {
		List<Iterable> iterables = asList(
			asList(1, 2, 3),
			asList('a', 'b')
		);
		Iterator<List> iterator = Combinatorics.combine(iterables);

		Assertions.assertThat(iterator).containsExactly(
			asList(1, 'a'), asList(1, 'b'),
			asList(2, 'a'), asList(2, 'b'),
			asList(3, 'a'), asList(3, 'b')
		);
	}
}
