package net.jqwik.docs;

import net.jqwik.api.*;

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

}
