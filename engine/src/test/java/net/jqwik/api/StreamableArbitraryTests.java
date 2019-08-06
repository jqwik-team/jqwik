package net.jqwik.api;

import java.util.*;

import net.jqwik.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ArbitraryTestHelper.*;

@Group
class StreamableArbitraryTests {

	@Group
	class Lists {

		@Example
		void createListWithSize() {

			SizableArbitrary<List<Integer>> listArbitrary =
				Arbitraries.integers().between(1, 5).list().ofMinSize(1).ofMaxSize(10);

			RandomGenerator<List<Integer>> generator = listArbitrary.generator(1000);

			assertAllGenerated(generator, list -> {
				assertThat(list.size()).isBetween(1, 10);
				assertThat(list).allMatch(i -> i >= 1 && i <= 5);
			});

		}

		@Example
		void reduceList() {
			StreamableArbitrary<Integer, List<Integer>> streamableArbitrary =
				Arbitraries.integers().between(1, 5).list().ofMinSize(1).ofMaxSize(10);

			Arbitrary<Integer> listArbitrary = streamableArbitrary
				.reduce(0, Integer::sum);

			RandomGenerator<Integer> generator = listArbitrary.generator(1000);

			assertAllGenerated(generator, sum -> {
				assertThat(sum).isBetween(1, 50);
			});

			assertAtLeastOneGenerated(generator, sum -> sum == 1);
			assertAtLeastOneGenerated(generator, sum -> sum > 40);
		}

	}
}
