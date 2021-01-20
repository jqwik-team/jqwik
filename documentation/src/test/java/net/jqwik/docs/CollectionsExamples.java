package net.jqwik.docs;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class CollectionsExamples {

	@Property
	void aListOfNonEmptyAlphaStringsWithSize5(@ForAll("alphaLists") List<String> alphaList) {
		Assertions.assertThat(alphaList).hasSize(5);
		Assertions.assertThat(alphaList).allMatch(string -> !string.isEmpty());
	}

	@Provide
	Arbitrary<List<String>> alphaLists() {
		return Arbitraries.strings().alpha().ofMinLength(1).list().ofSize(5);
	}

	@Property
	void aListOfNonEmptyAlphaStringsWithSize5_annotationsOnly(
			@ForAll @Size(5) List<@AlphaChars @NotEmpty String> alphaList
	) {
		Assertions.assertThat(alphaList).hasSize(5);
		Assertions.assertThat(alphaList).allMatch(string -> !string.isEmpty());
	}

}
