package net.jqwik.docs;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;

class InjectDuplicateExamples {

	Comparator<String> comparator = (s1, s2) -> {
		if (s1.length() + s2.length() == 0) return 0;
		if (s1.compareTo(s2) > 0) {
			return 1;
		} else {
			return -1;
		}
	};

	/**
	 * This property will miss an important case and therefore not reveal the bug
	 */
	@Property
	boolean comparing_strings_is_symmetric(@ForAll String first, @ForAll String second) {
		int comparison = comparator.compare(first, second);
		String comparisonRange = comparison < 0 ? "<0" : comparison > 0 ? ">0" : "=0";
		String empty = first.isEmpty() || second.isEmpty() ? "empty" : "not empty";
		Statistics.collect(comparisonRange, empty);
		return comparator.compare(second, first) == -comparison;
	}

	/**
	 * This property will reveal the bug
	 */
	@Property
	boolean comparing_strings_is_symmetric(@ForAll("pair") Tuple2<String, String> pair) {
		String first = pair.get1();
		String second = pair.get2();
		int comparison = comparator.compare(first, second);
		return comparator.compare(second, first) == -comparison;
	}

	@Provide
	Arbitrary<Tuple2<String, String>> pair() {
		StreamableArbitrary<String, List<String>> listOfTwoStrings =
			Arbitraries.strings().injectDuplicates(0.1).list().ofSize(2);
		return listOfTwoStrings.map(list -> Tuple.of(list.get(0), list.get(1)));
	}
}
