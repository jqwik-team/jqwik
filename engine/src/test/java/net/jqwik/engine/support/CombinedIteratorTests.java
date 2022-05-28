package net.jqwik.engine.support;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.engine.support.combinatorics.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

class CombinedIteratorTests {

	@Example
	void combineSmallIterables() {
		CombinedIterator<String> combined = new CombinedIterator<>(asList(
			asList("a", "b"),
			asList("c", "d", "e")
		));
		assertThat(combined.hasNext()).isTrue();
		assertThat(combined.next()).containsExactly("a", "c");
		assertThat(combined.hasNext()).isTrue();
		assertThat(combined.next()).containsExactly("a", "d");
		assertThat(combined.hasNext()).isTrue();
		assertThat(combined.next()).containsExactly("a", "e");
		assertThat(combined.hasNext()).isTrue();
		assertThat(combined.next()).containsExactly("b", "c");
		assertThat(combined.hasNext()).isTrue();
		assertThat(combined.next()).containsExactly("b", "d");
		assertThat(combined.hasNext()).isTrue();
		assertThat(combined.next()).containsExactly("b", "e");
		assertThat(combined.hasNext()).isFalse();
	}

	@Example
	void combineWithNullValues() {
		CombinedIterator<String> combined = new CombinedIterator<>(asList(
			asList("a", "b"),
			asList("c", null)
		));
		assertThat(combined.hasNext()).isTrue();
		assertThat(combined.next()).containsExactly("a", "c");
		assertThat(combined.hasNext()).isTrue();
		assertThat(combined.next()).containsExactly("a", null);
		assertThat(combined.hasNext()).isTrue();
		assertThat(combined.next()).containsExactly("b", "c");
		assertThat(combined.hasNext()).isTrue();
		assertThat(combined.next()).containsExactly("b", null);
		assertThat(combined.hasNext()).isFalse();
	}

	@Example
	void combineNoIterables() {
		CombinedIterator<String> combined = new CombinedIterator<>(asList());
		assertThat(combined.hasNext()).isFalse();
	}

	@Property
	void combineManyIterables(@ForAll @Size(min = 1, max = 10) List<@Size(min = 1, max = 5) Iterable<Integer>> iterables) {
		CombinedIterator<Integer> combined = new CombinedIterator<>(iterables);

		int expectedCount = 1;
		for (Iterable<Integer> iterable : iterables) {
			expectedCount *= count(iterable.iterator());
		}

		int count = count(combined);
		assertThat(count).isEqualTo(expectedCount);
	}

	private int count(Iterator<?> iterator) {
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		return count;
	}
}
