package net.jqwik.engine.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

class IgnoreGenerationErrorTests {

	@Example
	void simpleArbitrary() {
		Arbitrary<String> embedded = Arbitraries.fromGenerator(random -> {
			if (random.nextBoolean()) {
				throw new GenerationError(null);
			}
			return Shrinkable.unshrinkable("a");
		});
		IgnoreGenerationErrorArbitrary<String> arbitrary = new IgnoreGenerationErrorArbitrary<>(embedded);

		RandomGenerator<String> generator = arbitrary.generator(1000);

		ArbitraryTestHelper.assertAllGenerated(generator, s -> {return s.equals("a");});
	}

	@Example
	void mappedArbitrary() {
		Arbitrary<Integer> embedded = Arbitraries.integers().between(1, 10).map(i -> {
			if (i > 5) {
				throw new GenerationError(null);
			}
			return i;
		});
		IgnoreGenerationErrorArbitrary<Integer> arbitrary = new IgnoreGenerationErrorArbitrary<>(embedded);

		RandomGenerator<Integer> generator = arbitrary.generator(1000);

		ArbitraryTestHelper.assertAllGenerated(generator, s -> s <= 5);
	}

	@Example
	void combinedArbitrary() {

		Arbitrary<Integer> one = Arbitraries.constant(1);
		Arbitrary<Integer> two = Arbitraries.of(1, 2);

		Arbitrary<Integer> embedded = Combinators.combine(one, two).as((a, b) -> {
			if (b == 2) {
				throw new GenerationError(null);
			}
			return a + b;
		});
		IgnoreGenerationErrorArbitrary<Integer> arbitrary = new IgnoreGenerationErrorArbitrary<>(embedded);

		RandomGenerator<Integer> generator = arbitrary.generator(1000);

		ArbitraryTestHelper.assertAllGenerated(generator, s -> s == 2);
	}
}
