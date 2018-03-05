package net.jqwik.properties;

import java.util.*;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;

import net.jqwik.api.*;

@Group
class ArbitrariesWithSamplesTests {

	Random random = SourceOfRandomness.current();

	@Group
	class Generation {

		@Example
		void examplesAreGeneratedInRoundRobin() {
			Arbitrary<Integer> arbitrary = Arbitraries.samples(1, 2, 3);
			RandomGenerator<Integer> generator = arbitrary.generator(1);
			assertGeneratedSequence(generator, 1, 2, 3, 1, 2);
		}

		@Example
		void usingArbitraryWithExamplesGeneratesExamplesFirst() {
			Arbitrary<Integer> arbitrary = Arbitraries.of(5).withSamples(1, 2, 3);
			RandomGenerator<Integer> generator = arbitrary.generator(1);
			assertGeneratedSequence(generator, 1, 2, 3, 5, 5, 5);
		}

		@SafeVarargs
		private final <T> void assertGeneratedSequence(RandomGenerator<T> generator, T... sequence) {
			for (T expected : sequence) {
				Assertions.assertThat(generator.next(random).value()).isEqualTo(expected);
			}
		}
	}

	@Group
	class Shrinking {
		@Example
		void examplesAreShrunkDownToFirstExample() {
			Arbitrary<Integer> arbitrary = Arbitraries.samples(1, 2, 3, 4);
			RandomGenerator<Integer> generator = arbitrary.generator(1);

			Shrinkable<Integer> shrinkable1 = generator.next(random);
			Assertions.assertThat(shrunkValues(shrinkable1)).hasSize(0);

			Shrinkable<Integer> shrinkable2 = generator.next(random);
			Assertions.assertThat(shrunkValues(shrinkable2)).containsExactly(1);

			Shrinkable<Integer> shrinkable3 = generator.next(random);
			Assertions.assertThat(shrunkValues(shrinkable3)).containsExactly(2);

			Shrinkable<Integer> shrinkable4 = generator.next(random);
			Assertions.assertThat(shrunkValues(shrinkable4)).containsExactly(3);
		}

		@Example
		void withExamplesAreAlsoShrunkDownToFirstExample() {
			Arbitrary<Integer> arbitrary = Arbitraries.of(5).withSamples(1, 2, 3);
			RandomGenerator<Integer> generator = arbitrary.generator(1);

			Shrinkable<Integer> shrinkable1 = generator.next(random);
			Assertions.assertThat(shrunkValues(shrinkable1)).hasSize(0);

			Shrinkable<Integer> shrinkable2 = generator.next(random);
			Assertions.assertThat(shrunkValues(shrinkable2)).containsExactly(1);

			Shrinkable<Integer> shrinkable3 = generator.next(random);
			Assertions.assertThat(shrunkValues(shrinkable3)).containsExactly(2);

			Shrinkable<Integer> shrinkable5 = generator.next(random);
			Assertions.assertThat(shrunkValues(shrinkable5)).hasSize(0);
		}

		private Set<Integer> shrunkValues(Shrinkable<Integer> shrinkable) {
			return asValuesSet(shrinkable.shrinkNext(MockFalsifier.falsifyAll()));
		}

		private Set<Integer> asValuesSet(Set<ShrinkResult<Shrinkable<Integer>>> shrinkResults) {
			return shrinkResults.stream().map(result -> result.shrunkValue().value()).collect(Collectors.toSet());
		}

	}
}
