package net.jqwik.api.constraints;

import net.jqwik.api.*;

import static net.jqwik.api.arbitraries.CharacterArbitrary.*;

class NotBlankProperties {

	@Property
	boolean aStringIsNeverBlank(@ForAll @NotBlank String aString) {
		return isNotBlank(aString);
	}

	@Property
	boolean aProvidedStringIsNeverBlank(@ForAll("provided") @NotBlank String aString) {
		return isNotBlank(aString);
	}

	@Provide
	Arbitrary<String> provided() {
		return Arbitraries.strings().ofMaxLength(5);
	}

	@Property
	void doesNotInfluenceNonStringArbitrary(@ForAll @NotBlank long aLong) {
	}

	// Fixed test case from https://github.com/jqwik-team/jqwik/issues/663
	@Property(seed = "9077689816503037655")
	boolean testCaseFromIssue663(@ForAll @NotBlank String string) {
		return !isBlank(string);
	}

	private boolean isNotBlank(String aString) {
		return aString != null && !isBlank(aString);
	}
}
