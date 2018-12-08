package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

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
				assertThat(generator.next(random).value()).isEqualTo(expected);
			}
		}
	}

	@Group
	class Shrinking {
		@Example
		void examplesAreShrunkDownToFirstExample() {
			Arbitrary<Integer> arbitrary = Arbitraries.samples(1, 2, 3, 4);
			RandomGenerator<Integer> generator = arbitrary.generator(1);

			generator.next(random);
			generator.next(random);
			generator.next(random);
			Shrinkable<Integer> shrinkable4 = generator.next(random);
			assertThat(shrinkable4.value()).isEqualTo(4);

			ShrinkingSequence<Integer> sequence = shrinkable4.shrink(anInt -> false);

			assertThat(sequence.next(() -> {}, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(3);
			assertThat(sequence.next(() -> {}, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(2);
			assertThat(sequence.next(() -> {}, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(1);
			assertThat(sequence.next(() -> {}, ignore -> {})).isFalse();
		}

		@Example
		void withExamplesAreAlsoShrunkDownToFirstExample() {
			Arbitrary<Integer> arbitrary = Arbitraries.of(5).withSamples(1, 2, 3);
			RandomGenerator<Integer> generator = arbitrary.generator(1);

			generator.next(random);
			generator.next(random);
			Shrinkable<Integer> shrinkable5 = generator.next(random);
			assertThat(shrinkable5.value()).isEqualTo(3);

			ShrinkingSequence<Integer> sequence = shrinkable5.shrink(anInt -> false);

			assertThat(sequence.next(() -> {}, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(2);
			assertThat(sequence.next(() -> {}, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(1);
			assertThat(sequence.next(() -> {}, ignore -> {})).isFalse();
		}
	}
}
