package net.jqwik.properties;

import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class ExhaustiveGenerationTests {

	@Group
	class Integrals {

		@Example
		void integersFromMinToMax() {
			Optional<ExhaustiveGenerator<Integer>> optionalGenerator = Arbitraries.integers().between(-10, 10).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(21);
			assertThat(generator).containsExactly(-10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		}

		@Example
		void integerRangeTooBig() {
			Optional<ExhaustiveGenerator<Integer>> optionalGenerator = Arbitraries.integers().between(-1, Integer.MAX_VALUE).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

	}
}
