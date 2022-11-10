package net.jqwik.docs.arbitraryconfigurator;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class NotNullProperties {
	@Property
	void notNullStringsOnly(@ForAll("myStrings") @NotNull String aString) {
		Assertions.assertThat(aString).isNotNull();
	}

	@Provide
	Arbitrary<String> myStrings() {
		return Arbitraries.strings().alpha().injectNull(0.1);
	}
}
