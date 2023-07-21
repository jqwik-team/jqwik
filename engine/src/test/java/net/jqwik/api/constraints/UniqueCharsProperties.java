package net.jqwik.api.constraints;

import net.jqwik.api.*;

@PropertyDefaults(tries = 100)
class UniqueCharsProperties {

	@Property
	boolean defaultGeneratedString(@ForAll @UniqueChars String aString) {
		return hasNoDuplicates(aString);
	}

	@Property
	boolean stringFromSelection(@ForAll("selectionOfStrings") @UniqueChars String aString) {
		return hasNoDuplicates(aString);
	}

	@Provide
	Arbitrary<String> selectionOfStrings() {
		return Arbitraries.of(
			"abc",
			"xyz",
			"123",
			"aabbcc",
			"1abc1"
		);
	}

	private boolean hasNoDuplicates(String aString) {
		return aString.chars().distinct().count() == aString.length();
	}
}
