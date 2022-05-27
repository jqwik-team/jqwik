package net.jqwik.engine.properties.shrinking;

import java.util.ArrayList;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.testing.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingFalsifier.*;

/**
 * Inspired by https://github.com/HypothesisWorks/hypothesis/blob/master/hypothesis-python/tests/quality/test_shrink_quality.py
 * but still a lot of that missing
 */
class ShrinkingQualityProperties {

	@Property
	@ExpectFailure(checkResult = ShrinkTo2Elements.class)
	void reversingAList(@ForAll List<Integer> ls) {
		assertThat(reversed(ls)).isEqualTo(ls);
	}

	private class ShrinkTo2Elements extends ShrinkToChecker {
		@Override
		public Iterable<?> shrunkValues() {
			return asList(asList(0, 1));
		}
	}

	@Property(tries = 100)
	void reversingAList(@ForAll Random random) {
		Arbitrary<List<Integer>> integerLists = Arbitraries.integers().list();

		TestingFalsifier<List<Integer>> reverseEqualsOriginal = (List<Integer> list) -> list.equals(reversed(list));
		List<Integer> shrunkResult = falsifyThenShrink(integerLists, random, reverseEqualsOriginal);

		assertThat(shrunkResult).isEqualTo(asList(0, 1));
	}

	private List<Integer> reversed(final List<Integer> ls) {
		ArrayList<Integer> reversed = new ArrayList<>(ls);
		Collections.reverse(reversed);
		return reversed;
	}

	@Property(tries = 100)
	void largeUnionList(@ForAll Random random) {
		ListArbitrary<List<Integer>> listOfLists =
			Arbitraries.integers()
					   .list().ofMaxSize(50)
					   .list().ofMaxSize(50);

		TestingFalsifier<List<List<Integer>>> containsLessThan5DistinctNumbers = (List<List<Integer>> ls) -> {
			Set<Integer> allElements = new LinkedHashSet<>();
			for (List<Integer> x : ls) {
				allElements.addAll(x);
			}
			return allElements.size() < 5;
		};
		List<List<Integer>> shrunkResult = falsifyThenShrink(listOfLists, random, containsLessThan5DistinctNumbers);

		// e.g. [[0, 1, -1, 2, -2]]
		int numberOfElements = shrunkResult.stream().mapToInt(List::size).sum();
		assertThat(numberOfElements).isEqualTo(5);
		assertThat(numberOfElements).isLessThanOrEqualTo(10);
		assertThat(shrunkResult).hasSize(1);
	}

	@Property(seed = "42") // Fixed seed cause property failed in rare cases
	@ExpectFailure(checkResult = ShrinkTo3and3.class)
	boolean notEqual(@ForAll int i1, @ForAll int i2) {
		return i1 < 3 || i1 != i2;
	}

	private class ShrinkTo3and3 extends ShrinkToChecker {
		@Override
		public Iterable<?> shrunkValues() {
			return asList(3, 3);
		}
	}

	@Property(tries = 100)
	void flatMapRectangles(@ForAll Random random) {
		Arbitrary<Integer> lengths = Arbitraries.integers().between(0, 10);
		List<String> shrunkResult = falsifyThenShrink(
			lengths.flatMap(this::listsOfLength),
			random,
			falsifier(x -> !x.equals(asList("a", "b")))
		);

		assertThat(shrunkResult).containsExactly("a", "b");
	}

	private ListArbitrary<String> listsOfLength(int n) {
		return Arbitraries.of("a", "b").list().ofSize(n);
	}

	@Property(seed = "535353", tries = 10)
	void bound5(@ForAll Random random) {
		ListArbitrary<List<Short>> listOfLists = boundedListTuples();

		TestingFalsifier<List<List<Short>>> falsifier = p -> {
			short sum = (short) p.stream()
								 .flatMap(Collection::stream)
								 .mapToInt(i -> i)
								 .sum();
			return sum < 5 * 256;
		};

		List<List<Short>> shrunkResult = falsifyThenShrink(listOfLists, random, falsifier);

		assertThat(shrunkResult).hasSize(5);
		assertThat(shrunkResult).isEqualTo(asList(
			asList(),
			asList(),
			asList(),
			asList(),
			asList((short) -1, (short) -32768)
		));
	}

	@Provide
	ListArbitrary<List<Short>> boundedListTuples() {
		return Arbitraries.shorts()
						  .list()
						  .filter(x -> x.stream().mapToInt(s -> s).sum() < 256)
						  .list().ofSize(5);
	}

	@Property
	@ExpectFailure(checkResult = ShrinkToList900.class)
	void listLength(@ForAll("listOfIntegers") List<Integer> ls) {
		int max = ls.stream().mapToInt(i -> i).max().orElse(0);
		assertThat(max).isLessThan(900);
	}

	@Provide
	Arbitrary<List<Integer>> listOfIntegers() {
		return Arbitraries.integers().between(1, 100)
						  .flatMap(size -> Arbitraries.integers().between(0, 1000).list().ofSize(size));
	}

	private class ShrinkToList900 extends ShrinkToChecker {
		@Override
		public Iterable<?> shrunkValues() {
			return asList(asList(900));
		}
	}
}
