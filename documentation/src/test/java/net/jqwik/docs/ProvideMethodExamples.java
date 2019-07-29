package net.jqwik.docs;

import java.util.*;

import net.jqwik.api.*;

class ProvideMethodExamples {

	@Property
	boolean concatenatingStringWithInt(@ForAll("shortStrings") String aShortString, @ForAll("10 to 99") int aNumber) {
		String concatenated = aShortString + aNumber;
		return concatenated.length() > 2 && concatenated.length() < 11;
	}

	@Property
	boolean concatenatingStringWithInt2(@ForAll @From("shortStrings") String aShortString, @ForAll @From("10 to 99") int aNumber) {
		String concatenated = aShortString + aNumber;
		return concatenated.length() > 2 && concatenated.length() < 11;
	}

	@Property
	boolean joiningListOfStrings(@ForAll List<@From("shortStrings") String> listOfStrings) {
		String concatenated = String.join("", listOfStrings);
		return concatenated.length() <= 8 * listOfStrings.size();
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
