package net.jqwik.api.domains;

import java.util.*;

import net.jqwik.api.*;

class ContextWithDependentProviders extends DomainContextBase {

	@Provide
	Arbitrary<Integer> from1to10() {
		return Arbitraries.integers().between(1, 10);
	}

	@Provide
	Arbitrary<List<Integer>> listOf5Ints(@ForAll int value) {
		return Arbitraries.just(value).list().ofSize(5);
	}

	@Provide
	Arbitrary<Character> a() {
		return Arbitraries.just('a');
	}

	@Provide
	Arbitrary<Character> z() {
		return Arbitraries.just('z');
	}

	@Provide
	Arbitrary<String> doubleStrings(@ForAll("a") Character base) {
		return Arbitraries.just(base + "" + base);
	}

}
