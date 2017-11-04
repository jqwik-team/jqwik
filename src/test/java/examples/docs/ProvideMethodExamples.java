package examples.docs;

import net.jqwik.api.*;

class ProvideMethodExamples {

	@Property
	boolean concatenatingStringWithInt(
		@ForAll("shortStrings") String aShortString,
		@ForAll("10 to 99") int aNumber
	) {
		String concatenated = aShortString + aNumber;
		return concatenated.length() > 2 && concatenated.length() < 11;
	}

	@Provide
	Arbitrary<String> shortStrings() {
		return Arbitraries.strings('a', 'z', 1, 8);
	}

	@Provide("10 to 99")
	Arbitrary<Integer> numbers() {
		return Arbitraries.integers(10, 99);
	}
}
