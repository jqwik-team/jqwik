package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;

import static net.jqwik.properties.ArbitraryTestHelper.*;

class ArbitraryShrinkingTests {

	@Property(tries = 10)
	void values(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3);
		assertAllValuesAreShrunkTo(1, arbitrary, random);
	}

	@Property(tries = 10)
	void withExamples(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3) //
			.withSamples(100, 200, 300);
		assertAllValuesAreShrunkTo(100, arbitrary, random);
	}

	@Property(tries = 10)
	void filtered(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //
			.filter(i -> i % 2 == 0);
		assertAllValuesAreShrunkTo(2, arbitrary, random);
	}

	@Property(tries = 10)
	void unique(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //
			.unique();
		assertAllValuesAreShrunkTo(1, arbitrary, random);
	}

	@Property(tries = 10)
	void uniqueInSet(@ForAll Random random) {
		Arbitrary<Set<Integer>> arbitrary = Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //
			.unique().set().ofSize(3);
		assertAllValuesAreShrunkTo(new HashSet<>(Arrays.asList(1, 2, 3)), arbitrary, random);
	}

	@Property(tries = 10)
	void mapped(@ForAll Random random) {
		Arbitrary<String> arbitrary = Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //
			.map(String::valueOf);
		assertAllValuesAreShrunkTo("1", arbitrary, random);
	}

	@Property(tries = 10)
	void flatMapped(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //
			.flatMap(i -> Arbitraries.of(i));
		assertAllValuesAreShrunkTo(1, arbitrary, random);
	}

}
