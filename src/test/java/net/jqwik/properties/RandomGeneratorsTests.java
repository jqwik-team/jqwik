package net.jqwik.properties;

import java.util.Set;

import net.jqwik.api.Example;
import net.jqwik.properties.arbitraries.RandomGenerators;
import org.assertj.core.api.Assertions;

class RandomGeneratorsTests {

	// TODO: Move generator tests from ArbitrariesTests here

	@Example
	void setsAreGeneratedWithCorrectMinAndMaxSize() {
		RandomGenerator<Integer> integerGenerator = RandomGenerators.choose(1, 10);
		RandomGenerator<Set<Integer>> generator = RandomGenerators.set(integerGenerator, 2, 5);
		ArbitraryTestHelper.assertAllGenerated(generator, set -> set.size() >= 2 && set.size() <= 5);
	}
}
