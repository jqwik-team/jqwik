package net.jqwik.api.constraints;

import net.jqwik.api.*;

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

	private boolean isNotBlank(String aString) {
		return aString != null && !aString.trim().isEmpty();
	}

}
