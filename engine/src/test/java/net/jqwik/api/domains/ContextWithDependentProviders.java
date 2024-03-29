package net.jqwik.api.domains;

import java.util.*;

import org.assertj.core.api.*;

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
		Assertions.assertThat(base).isEqualTo('a');
		return Arbitraries.just(base + "" + base);
	}

	@Provide
	Arbitrary<Double> doubles(@ForAll(supplier = MinValues.class) double min) {
		Assertions.assertThat(min).isBetween(10.0, 100.0);
		return Arbitraries.doubles().greaterOrEqual(min);
	}

	class MinValues implements ArbitrarySupplier<Double> {
		@Override
		public Arbitrary<Double> get() {
			return Arbitraries.doubles().between(10.0, 100.0);
		}
	}
}
