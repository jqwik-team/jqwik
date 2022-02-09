package net.jqwik.docs;

import net.jqwik.api.*;

import java.util.*;

class FluentConfigurationExamples {

	@Provide
	Arbitrary<String> alphaNumericStringsWithMinLength5() {
		return Arbitraries.strings().ofMinLength(5).alpha().numeric();
	}

	@Provide
	Arbitrary<List<? extends Integer>> fixedSizedListOfPositiveIntegers() {
		return Arbitraries.integers().greaterOrEqual(0).list().ofSize(17);
	}
}
