package net.jqwik.api.edgeCases;

import java.math.*;
import java.util.*;
import java.util.function.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
class NumbersEdgeCasesTests {

	private <T> Set<T> values(EdgeCases<T> edgeCases) {
		Set<T> values = new LinkedHashSet<>();
		for (Shrinkable<T> edgeCase : edgeCases) {
			values.add(edgeCase.value());
		}
		return values;
	}

	@Group
	class Decimals implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				Arbitraries.bigDecimals(),
				Arbitraries.floats(),
				Arbitraries.doubles()
			);
		}

		@Example
		void bigDecimals() {
			int scale = 2;
			BigDecimalArbitrary arbitrary = Arbitraries.bigDecimals()
													   .between(BigDecimal.valueOf(-10), BigDecimal.valueOf(10))
													   .ofScale(scale);
			EdgeCases<BigDecimal> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					BigDecimal.valueOf(-10).setScale(2),
					BigDecimal.valueOf(-1).setScale(2),
					BigDecimal.valueOf(-0.01),
					BigDecimal.ZERO.setScale(2),
					BigDecimal.valueOf(0.01),
					BigDecimal.valueOf(1).setScale(2),
					BigDecimal.valueOf(10).setScale(2)
			);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(7);
		}

		@Example
		void bigDecimalsWithExcludedBorders() {
			int scale = 1;
			BigDecimalArbitrary arbitrary = Arbitraries.bigDecimals()
													   .between(BigDecimal.valueOf(-10), false, BigDecimal.valueOf(10), false)
													   .ofScale(scale);
			EdgeCases<BigDecimal> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					BigDecimal.valueOf(-9.9),
					BigDecimal.valueOf(-1).setScale(1),
					BigDecimal.valueOf(-0.1),
					BigDecimal.ZERO.setScale(1),
					BigDecimal.valueOf(0.1),
					BigDecimal.valueOf(1).setScale(1),
					BigDecimal.valueOf(9.9)
			);
		}

		@Example
		void bigDecimalsWithShrinkingTarget() {
			int scale = 1;
			BigDecimalArbitrary arbitrary = Arbitraries.bigDecimals()
													   .between(BigDecimal.valueOf(1), BigDecimal.valueOf(10))
													   .ofScale(scale)
													   .shrinkTowards(BigDecimal.valueOf(5));
			EdgeCases<BigDecimal> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					BigDecimal.valueOf(1).setScale(1),
					BigDecimal.valueOf(4.9),
					BigDecimal.valueOf(5).setScale(1),
					BigDecimal.valueOf(5.1),
					BigDecimal.valueOf(10).setScale(1)
			);
		}

		@Group
		class DecimalsConfiguration {

			@Example
			void addedBigDecimalsEdgeCasesAreShrinkable() {
				Arbitrary<BigDecimal> arbitrary =
						Arbitraries
								.bigDecimals()
								.between(BigDecimal.valueOf(-100), BigDecimal.valueOf(100))
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.includeOnly();
									edgeCasesConfig.add(BigDecimal.valueOf(42.41));
								});

				EdgeCases<BigDecimal> edgeCases = arbitrary.edgeCases();
				assertThat(values(edgeCases)).containsExactlyInAnyOrder(BigDecimal.valueOf(42.41));

				Shrinkable<BigDecimal> shrinkable = edgeCases.suppliers().get(0).get();
				assertThat(shrinkable.shrink()).isNotEmpty();
			}

			@Example
			void addingEdgeCaseOutsideAllowedRangeFails() {
				Arbitrary<BigDecimal> arbitrary =
						Arbitraries
								.bigDecimals()
								.between(BigDecimal.valueOf(-100), BigDecimal.valueOf(100))
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.add(BigDecimal.valueOf(100.1));
								});

				assertThatThrownBy(() -> arbitrary.generator(100, true)).isInstanceOf(IllegalArgumentException.class);
			}

			@Example
			void addedDoubleEdgeCasesAreShrinkable() {
				Arbitrary<Double> arbitrary =
						Arbitraries
								.doubles()
								.between(-100, 100)
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.includeOnly();
									edgeCasesConfig.add(42.41);
								});

				EdgeCases<Double> edgeCases = arbitrary.edgeCases();
				assertThat(edgeCases.size()).isEqualTo(1);
				assertThat(values(edgeCases)).containsExactlyInAnyOrder(42.41);

				Shrinkable<Double> shrinkable = edgeCases.suppliers().get(0).get();
				assertThat(shrinkable.shrink()).isNotEmpty();
			}

			@Example
			void addedFloatEdgeCasesAreShrinkable() {
				Arbitrary<Float> arbitrary =
						Arbitraries
								.floats()
								.between(-100, 100)
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.includeOnly();
									edgeCasesConfig.add(42.41f);
								});

				EdgeCases<Float> edgeCases = arbitrary.edgeCases();
				assertThat(edgeCases.size()).isEqualTo(1);
				assertThat(values(edgeCases)).containsExactlyInAnyOrder(42.41f);

				Shrinkable<Float> shrinkable = edgeCases.suppliers().get(0).get();
				assertThat(shrinkable.shrink()).isNotEmpty();
			}

		}

	}

	@Group
	class Integrals implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				Arbitraries.integers(),
				Arbitraries.shorts(),
				Arbitraries.bytes(),
				Arbitraries.longs(),
				Arbitraries.bigIntegers()
			);
		}

		@Example
		void intEdgeCases() {
			IntegerArbitrary arbitrary = Arbitraries.integers().between(-10, 10);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					-10, -9, -2, -1, 0, 1, 2, 9, 10
			);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(9);
		}

		@Example
		void intOnlyPositive() {
			IntegerArbitrary arbitrary = Arbitraries.integers().between(5, 100);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					5, 6, 99, 100
			);
		}

		@Example
		void intWithShrinkTarget() {
			IntegerArbitrary arbitrary = Arbitraries.integers().between(5, 100).shrinkTowards(42);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					5, 6, 42, 99, 100
			);
		}

		@Example
		void shorts() {
			ShortArbitrary arbitrary = Arbitraries.shorts().between((short) -5, (short) 5);
			EdgeCases<Short> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					(short) -5, (short) -4, (short) -2, (short) -1, (short) 0, (short) 1, (short) 2, (short) 4, (short) 5
			);
		}

		@Example
		void bytes() {
			ByteArbitrary arbitrary = Arbitraries.bytes().between((byte) -5, (byte) 5);
			EdgeCases<Byte> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					(byte) -5, (byte) -4, (byte) -2, (byte) -1, (byte) 0, (byte) 1, (byte) 2, (byte) 4, (byte) 5
			);
		}

		@Example
		void longs() {
			LongArbitrary arbitrary = Arbitraries.longs().between(-5, 5);
			EdgeCases<Long> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					(long) -5, (long) -4, (long) -2, (long) -1, (long) 0, (long) 1, (long) 2, (long) 4, (long) 5
			);
		}

		@Example
		void bigIntegers() {
			BigIntegerArbitrary arbitrary = Arbitraries.bigIntegers().between(BigInteger.valueOf(-5), BigInteger.valueOf(5));
			EdgeCases<BigInteger> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
					BigInteger.valueOf(-5),
					BigInteger.valueOf(-4),
					BigInteger.valueOf(-2),
					BigInteger.valueOf(-1),
					BigInteger.valueOf(0),
					BigInteger.valueOf(1),
					BigInteger.valueOf(2),
					BigInteger.valueOf(4),
					BigInteger.valueOf(5)
			);
		}

		@Group
		class IntegralsConfiguration {

			@Example
			void noEdgeCases() {
				Arbitrary<Integer> arbitrary =
						Arbitraries
								.integers()
								.between(-100, 100)
								.edgeCases(edgeCasesConfig -> edgeCasesConfig.none());

				EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
				Assertions.assertThat(values(edgeCases)).isEmpty();

				// Random value generation still works
				assertAllGenerated(
						arbitrary.generator(1000, true),
						SourceOfRandomness.current(),
						(Consumer<Integer>) i -> assertThat(i).isBetween(-100, 100)
				);
			}

			@Example
			void filter() {
				Arbitrary<Integer> arbitrary =
						Arbitraries
								.integers()
								.between(-100, 100)
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.filter(i -> i % 2 == 0);
									edgeCasesConfig.filter(i -> i >= 0);
								});

				EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
				Assertions.assertThat(values(edgeCases)).containsExactlyInAnyOrder(0, 2, 100);
			}

			@Example
			void addingEdgeCases() {
				Arbitrary<Integer> arbitrary =
						Arbitraries
								.integers()
								.between(-100, 100)
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.add(42);
									edgeCasesConfig.add(41);
								});

				EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
				Assertions.assertThat(values(edgeCases)).containsExactlyInAnyOrder(-1, 0, -2, 1, 2, -99, -100, 99, 100, 41, 42);
			}

			@Example
			void addingEdgeCaseOutsideAllowedRangeFails() {
				Arbitrary<Integer> arbitrary =
						Arbitraries
								.integers()
								.between(-100, 100)
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.add(-101);
								});

				assertThatThrownBy(() -> arbitrary.generator(100, true)).isInstanceOf(IllegalArgumentException.class);
			}

			@Example
			void combineFilterAndAdd() {
				Arbitrary<Integer> arbitrary =
						Arbitraries
								.integers()
								.between(-100, 100)
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.filter(i -> i < 0);
									edgeCasesConfig.add(42);
									edgeCasesConfig.add(41);
								});

				EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
				Assertions.assertThat(values(edgeCases)).containsExactlyInAnyOrder(-1, -2, -99, -100, 41, 42);

			}

			@Example
			void includeOnly() {
				Arbitrary<Integer> arbitrary =
						Arbitraries
								.integers()
								.between(-100, 100)
								.edgeCases(edgeCasesConfig -> edgeCasesConfig.includeOnly(-100, 100));

				EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
				Assertions.assertThat(values(edgeCases)).containsExactlyInAnyOrder(-100, 100);
			}

			@Example
			void addedIntegerEdgeCasesAreShrinkable() {
				Arbitrary<Integer> arbitrary =
						Arbitraries
								.integers()
								.between(-100, 100)
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.includeOnly();
									edgeCasesConfig.add(42);
								});

				EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
				assertThat(values(edgeCases)).containsExactlyInAnyOrder(42);

				Shrinkable<Integer> shrinkable = edgeCases.suppliers().get(0).get();
				assertThat(shrinkable.shrink()).isNotEmpty();
			}

			@Example
			void addedLongEdgeCasesAreShrinkable() {
				Arbitrary<Long> arbitrary =
						Arbitraries
								.longs()
								.between(-100L, 100L)
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.includeOnly();
									edgeCasesConfig.add(42L);
								});

				EdgeCases<Long> edgeCases = arbitrary.edgeCases();
				assertThat(values(edgeCases)).containsExactlyInAnyOrder(42L);

				Shrinkable<Long> shrinkable = edgeCases.suppliers().get(0).get();
				assertThat(shrinkable.shrink()).isNotEmpty();
			}

			@Example
			void addedShortEdgeCasesAreShrinkable() {
				Arbitrary<Short> arbitrary =
						Arbitraries
								.shorts()
								.between((short) -100, (short) 100)
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.includeOnly();
									edgeCasesConfig.add((short) 42);
								});

				EdgeCases<Short> edgeCases = arbitrary.edgeCases();
				assertThat(values(edgeCases)).containsExactlyInAnyOrder((short) 42);

				Shrinkable<Short> shrinkable = edgeCases.suppliers().get(0).get();
				assertThat(shrinkable.shrink()).isNotEmpty();
			}

			@Example
			void addedByteEdgeCasesAreShrinkable() {
				Arbitrary<Byte> arbitrary =
						Arbitraries
								.bytes()
								.between((byte) -100, (byte) 100)
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.includeOnly();
									edgeCasesConfig.add((byte) 42);
								});

				EdgeCases<Byte> edgeCases = arbitrary.edgeCases();
				assertThat(values(edgeCases)).containsExactlyInAnyOrder((byte) 42);

				Shrinkable<Byte> shrinkable = edgeCases.suppliers().get(0).get();
				assertThat(shrinkable.shrink()).isNotEmpty();
			}

			@Example
			void addedBigIntegerEdgeCasesAreShrinkable() {
				Arbitrary<BigInteger> arbitrary =
						Arbitraries
								.bigIntegers()
								.between(BigInteger.valueOf(-100), BigInteger.valueOf(100))
								.edgeCases(edgeCasesConfig -> {
									edgeCasesConfig.includeOnly();
									edgeCasesConfig.add(BigInteger.valueOf(42));
								});

				EdgeCases<BigInteger> edgeCases = arbitrary.edgeCases();
				assertThat(values(edgeCases)).containsExactlyInAnyOrder(BigInteger.valueOf(42));

				Shrinkable<BigInteger> shrinkable = edgeCases.suppliers().get(0).get();
				assertThat(shrinkable.shrink()).isNotEmpty();
			}

		}
	}

}
