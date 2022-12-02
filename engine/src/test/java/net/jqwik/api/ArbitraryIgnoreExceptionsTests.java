package net.jqwik.api;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.*;
import net.jqwik.api.edgeCases.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

class ArbitraryIgnoreExceptionsTests {

	private static class OtherException extends RuntimeException {
		OtherException(String message) {
			super(message);
		}
	}


	@Example
	void ignoreIllegalArgumentException(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5)
				.map(anInt -> {
					if (anInt % 2 == 0) {
						throw new IllegalArgumentException("No even numbers");
					}
					return anInt;
				});
		Arbitrary<Integer> filtered = arbitrary.ignoreException(IllegalArgumentException.class);
		RandomGenerator<Integer> generator = filtered.generator(10, true);

		assertThat(generator.next(random).value()).isEqualTo(1);
		assertThat(generator.next(random).value()).isEqualTo(3);
		assertThat(generator.next(random).value()).isEqualTo(5);
		assertThat(generator.next(random).value()).isEqualTo(1);
	}

	@Example
	void ignoreMultipleExceptions(@ForAll Random random) {

		Arbitrary<Integer> arbitrary =
			new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5, 6, 7, 8, 9)
				.map(anInt -> {
					if (anInt % 2 == 0) {
						throw new IllegalArgumentException("No even numbers");
					}
					if (anInt % 3 == 0) {
						throw new OtherException("Not divisible by 3");
					}
					return anInt;
				});
		//noinspection unchecked
		Arbitrary<Integer> filtered = arbitrary.ignoreExceptions(
			IllegalArgumentException.class,
			OtherException.class
		);
		RandomGenerator<Integer> generator = filtered.generator(10, true);

		assertThat(generator.next(random).value()).isEqualTo(1);
		assertThat(generator.next(random).value()).isEqualTo(5);
		assertThat(generator.next(random).value()).isEqualTo(7);
		assertThat(generator.next(random).value()).isEqualTo(1);
	}

	@Example
	void ignoreSubtypeOfException(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5)
				.map(anInt -> {
					if (anInt % 2 == 0) {
						throw new IllegalArgumentException("No even numbers");
					}
					return anInt;
				});
		Arbitrary<Integer> filtered = arbitrary.ignoreException(RuntimeException.class);
		RandomGenerator<Integer> generator = filtered.generator(10, true);

		assertThat(generator.next(random).value()).isEqualTo(1);
		assertThat(generator.next(random).value()).isEqualTo(3);
		assertThat(generator.next(random).value()).isEqualTo(5);
		assertThat(generator.next(random).value()).isEqualTo(1);
	}

	@Example
	void failIfFilterWillDiscard10000ValuesInARow(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5)
				.map(anInt -> {
					throw new IllegalArgumentException("No even numbers");
				});
		Arbitrary<Integer> filtered = arbitrary.ignoreException(RuntimeException.class);
		RandomGenerator<Integer> generator = filtered.generator(10, true);

		assertThatThrownBy(() -> generator.next(random).value()).isInstanceOf(JqwikException.class);
	}

	@Example
	void exhaustiveGeneration() {
		Arbitrary<Integer> arbitrary =
			Arbitraries.integers().between(-5, 5)
					   .map(anInt -> {
						   if (anInt % 2 != 0) {
							   throw new IllegalArgumentException("No even numbers");
						   }
						   return anInt;
					   });
		Arbitrary<Integer> filtered = arbitrary.ignoreException(IllegalArgumentException.class);

		Optional<ExhaustiveGenerator<Integer>> optionalGenerator = filtered.exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(11); // Cannot know the number of thrown exceptions in advance
		assertThat(generator).containsExactly(-4, -2, 0, 2, 4);
	}

	@Group
	class Shrinking {

		@Property(tries = 10)
		void singleException(@ForAll Random random) {
			Arbitrary<Integer> arbitrary =
				Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
						   .map(i -> {
							   if (i % 2 != 0) {
								   throw new IllegalArgumentException("Only even numbers");
							   }
							   return i;
						   })
						   .ignoreException(IllegalArgumentException.class);

			Integer value = falsifyThenShrink((Arbitrary<? extends Integer>) arbitrary, random);
			Assertions.assertThat(value).isEqualTo(2);
		}

		@SuppressWarnings("unchecked")
		@Property(tries = 10)
		void severalExceptions(@ForAll Random random) {
			Arbitrary<Integer> arbitrary =
				Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
						   .map(i -> {
							   if (i % 2 != 0) {
								   throw new IllegalArgumentException("Only even numbers");
							   }
							   if (i == 2) {
								   throw new OtherException("Not 2");
							   }
							   return i;
						   })
						   .ignoreExceptions(IllegalArgumentException.class, OtherException.class);

			Integer value = falsifyThenShrink((Arbitrary<? extends Integer>) arbitrary, random);
			Assertions.assertThat(value).isEqualTo(4);
		}
	}

	@Group
	class GenerationTests implements GenericGenerationProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.ofSuppliers(
				() -> Arbitraries.integers().map(i -> {
					if (i % 2 == 0) throw new RuntimeException();
					return i;
				}).ignoreException(RuntimeException.class)
			);
		}
	}

	@Label("EdgeCases")
	@Group
	class EdgeCasesTests implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				ignoringExceptionArbitrary()
			);
		}

		@Example
		void edgeCases() {
			Arbitrary<Integer> arbitrary = ignoringExceptionArbitrary();
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				-10, -2, 0, 2, 10
			);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(5);
		}

		private Arbitrary<Integer> ignoringExceptionArbitrary() {
			Arbitrary<Integer> arbitrary =
				Arbitraries.integers().between(-10, 10)
						   .map(i -> {
							   if (i % 2 != 0) {
								   throw new IllegalArgumentException("Only even numbers");
							   }
							   return i;
						   })
						   .ignoreException(IllegalArgumentException.class);
			return arbitrary;
		}
	}
}
