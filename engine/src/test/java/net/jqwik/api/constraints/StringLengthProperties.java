package net.jqwik.api.constraints;

import java.util.*;

import net.jqwik.api.*;

class StringLengthProperties {

	@Property
	boolean aStringWithConstrainedLength(@ForAll @StringLength(min = 2, max = 7) String aString) {
		return aString.length() >= 2 && aString.length() <= 7;
	}

	@Property
	boolean fixedSize(@ForAll @StringLength(5) String aString) {
		return aString.length() == 5;
	}

	@Property
	boolean minCanBeUsedWithoutMax(@ForAll @StringLength(min = 2) String aString) {
		return aString.length() >= 2;
	}

	@Property
	@Label("@NotEmpty is like @StringLength(min = 1)")
	boolean notEmptyIsLikeMinLength1(@ForAll @NotEmpty @StringLength(max = 7) String aString) {
		return aString.length() >= 1 && aString.length() <= 7;
	}

	@Property
	boolean notEmptyInEmbeddedString(@ForAll List<@AlphaChars @NotEmpty String> aList) {
		return aList.stream().allMatch(aString -> !aString.isEmpty());
	}

	@Property
	boolean maxCanBeUsedWithoutMin(@ForAll @StringLength(max = 7) String aString) {
		return aString.length() <= 7;
	}

	@Property
	boolean stringLengthAlsoWorksWithProvidedArbitrary(@ForAll("provided") @StringLength(max = 5) String aString) {
		return aString.length() <= 5;
	}

	@Provide
	Arbitrary<String> provided() {
		return Arbitraries.longs().greaterOrEqual(0).map(l -> Long.toString(l));
	}

	@Property
	void doesNotInfluenceNonStringArbitrary(@ForAll @StringLength(max = 5) long aLong) {
	}

}
