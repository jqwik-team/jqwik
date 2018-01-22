package net.jqwik.properties;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import java.util.*;

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

	private <T> void assertAllValuesAreShrunkTo(T expectedShrunkValue, Arbitrary<T> arbitrary, @ForAll Random random) {
		Shrinkable<T> shrinkable = arbitrary.generator(10).next(random);
		ShrinkResult<Shrinkable<T>> shrunk = new ValueShrinker<>(shrinkable).shrink(value -> false, null);
		Assertions.assertThat(shrunk.shrunkValue().value()).isEqualTo(expectedShrunkValue);
	}
}
