package examples.docs;

import net.jqwik.api.*;

class ProvideMethodExamples {

	@Property
	boolean concatenatingStringWithInt(@ForAll("shortStrings") String aShortString, @ForAll("10 to 99") int aNumber) {
		String concatenated = aShortString + aNumber;
		return concatenated.length() > 2 && concatenated.length() < 11;
	}

	@Provide
	Arbitrary<String> shortStrings() {
		return Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1).ofMaxLength(8);
	}

	@Provide("10 to 99")
	Arbitrary<Integer> numbers() {
		return Arbitraries.integers().between(10, 99);
	}
}
