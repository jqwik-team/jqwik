package examples.docs;

import net.jqwik.api.*;

class MappingAndCombinatorExamples {

	@Property
	boolean fiveDigitsAreAlways5Long(@ForAll("fiveDigitStrings") String numericalString) {
		return numericalString.length() == 5;
	}

	@Provide
	Arbitrary<String> fiveDigitStrings() {
		return Arbitraries.integers(10000, 99999).map(aNumber -> String.valueOf(aNumber));
	}

}
