package net.jqwik.engine.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

class IgnoreGenerationErrorTests {

	@Example
	void simpleArbitrary() {
		Arbitrary<String> embedded = Arbitraries.fromGenerator(random -> {
			if (random.nextBoolean()) {
				throw new RuntimeException();
			}
			return Shrinkable.unshrinkable("a");
		});
		IgnoreGenerationErrorArbitrary<String> arbitrary = new IgnoreGenerationErrorArbitrary<>(embedded);

		RandomGenerator<String> generator = arbitrary.generator(1000);

		ArbitraryTestHelper.assertAllGenerated(generator, s -> {return s.equals("a");});
	}
}
