package net.jqwik.properties;

import static net.jqwik.properties.ArbitraryTestHelper.*;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;

class ArbitraryTests {

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

	@Group
	class Shrinking {

		@Example
		void shrinkingMappedArbitraryCanOnlyShrinkToOriginalValue() {
			Arbitrary<String> mappedArbitrary = Arbitraries.integer(0, 100).map(i -> "i:" + i);
			Shrinkable<String> mappedShrinkable = mappedArbitrary.shrinkableFor("i:10");

			MockFalsifier<String> falsifier = MockFalsifier.falsifyAll();
			Optional<ShrinkResult<String>> shrinkResult = mappedShrinkable.shrink(falsifier);
			assertThat(shrinkResult).isPresent();
			assertThat(shrinkResult.get().value()).isEqualTo("i:10");
			assertThat(falsifier.visited()).containsExactly("i:10");

		}

		// Disabled until new integrated shrinking is in place
		// @Example
		void shrinkingFilteredArbitrary() {
			Arbitrary<Integer> filteredArbitrary = Arbitraries.integer(0, 100).filter(i -> i % 2 == 0);
			Shrinkable<Integer> mappedShrinkable = filteredArbitrary.shrinkableFor(10);

			MockFalsifier<Integer> falsifier = MockFalsifier.falsifyAll();
			Optional<ShrinkResult<Integer>> shrinkResult = mappedShrinkable.shrink(falsifier);
			assertThat(shrinkResult).isPresent();
			assertThat(shrinkResult.get().value()).isEqualTo(0);
			assertThat(falsifier.visited()).containsExactly(10, 2, 0);
		}
	}

}
