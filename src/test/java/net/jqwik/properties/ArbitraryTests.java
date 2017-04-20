package net.jqwik.properties;

import static net.jqwik.properties.ArbitraryTestHelper.*;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

public class ArbitraryTests {

	private Random random = new Random();

	@Example
	void filtering() {
		Arbitrary<Integer> count = new CountingArbitrary();
		Arbitrary<Integer> countEven = count.filter(i -> i % 2 == 0);

		RandomGenerator<Integer> generator = countEven.generator(1);
		assertGenerated(generator, 2, 4, 6, 8);
	}

	@Example
	void mapping() {
		Arbitrary<Integer> count = new CountingArbitrary();
		Arbitrary<String> countStrings = count.map(i -> "i=" + i);

		RandomGenerator<String> generator = countStrings.generator(1);
		assertGenerated(generator, "i=1", "i=2", "i=3");
	}

	@Example
	void withNullInjectsNullValues() {
		Arbitrary<Integer> count = new CountingArbitrary();
		Arbitrary<Integer> withNull = count.injectNull(0.5);

		RandomGenerator<Integer> generator = withNull.generator(1);
		assertAtLeastOneGenerated(generator, Objects::isNull);
	}

	@Example
	void withSamplesGeneratesSamplesFirst() {
		Arbitrary<Integer> count = new CountingArbitrary();
		Arbitrary<Integer> countWithSamples = count.withSamples(10, 9, 8);
		assertGenerated(countWithSamples.generator(1), 10, 9, 8, 1, 2, 3);
	}

}
