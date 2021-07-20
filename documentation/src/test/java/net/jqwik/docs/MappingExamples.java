package net.jqwik.docs;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

class MappingExamples {

	@Property
	boolean evenNumbersThroughMapping(@ForAll("evenNumbers") int aNumber) {
		return aNumber % 2 == 0;
	}

	@Provide
	Arbitrary<Integer> evenNumbers() {
		return Arbitraries.integers().map(i -> i * 2);
	}

	@Property
	boolean fiveDigitsAreAlways5Long(@ForAll("fiveDigitStrings") String numericalString) {
		return numericalString.length() == 5;
	}

	@Provide
	Arbitrary<String> fiveDigitStrings() {
		return Arbitraries.integers().between(10000, 99999).map(aNumber -> String.valueOf(aNumber));
	}

	@Property
	void elementsAreCorrectlyLocated(@ForAll("elementsWithOccurrence") List<Tuple2<Integer, Long>> list) {
		Assertions.assertThat(list).allMatch(t -> t.get2() <= list.size());
	}

	@Provide
	Arbitrary<List<Tuple2<Integer, Long>>> elementsWithOccurrence() {
		return Arbitraries.integers().between(10000, 99999).list()
						  .mapEach((all, i) -> {
							  long count = all.stream().filter(e -> e.equals(i)).count();
							  return Tuple.of(i, count);
						  });
	}

}
