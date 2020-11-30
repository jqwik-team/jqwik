package net.jqwik.api.constraints;

import java.math.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;

@PropertyDefaults(tries = 100)
class RangeProperties {

	@Property(generation = GenerationMode.RANDOMIZED)
	void doesNotBreakNonNumberArbitrary(@ForAll @IntRange(min = 2, max = 7) String string) {
	}

	@Group
	class ByteRanges {

		@Property
		boolean bytes(@ForAll @ByteRange(min = 2, max = 7) byte value) {
			return value >= 2 && value <= 7;
		}

		@Property
		boolean boxedBytes(@ForAll @ByteRange(min = 2, max = 7) Byte value) {
			return value >= 2 && value <= 7;
		}

		@Property
		boolean bytesMinOnly(@ForAll @ByteRange(min = 50) byte value) {
			return value >= 50 && value <= Byte.MAX_VALUE;
		}

		@Property(generation = GenerationMode.RANDOMIZED)
		boolean alsoWorksForProvidedArbitrary(@ForAll("provided") @ByteRange(min = 2, max = 7) byte value) {
			return value >= 2 && value <= 7;
		}

		@Provide
		Arbitrary<Byte> provided() {
			return Arbitraries.of((byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 7, (byte) 8, (byte) 10, (byte) 100);
		}

	}

	@Group
	class ShortRanges {

		@Property
		boolean shorts(@ForAll @ShortRange(min = 2, max = 7) short value) {
			return value >= 2 && value <= 7;
		}

		@Property
		boolean boxedShorts(@ForAll @ShortRange(min = 2, max = 7) Short value) {
			return value >= 2 && value <= 7;
		}

		@Property
		boolean shortsMinOnly(@ForAll @ShortRange(min = -2000) short value) {
			return value >= -2000 && value <= Short.MAX_VALUE;
		}

		@Property(generation = GenerationMode.RANDOMIZED)
		boolean alsoWorksForProvidedArbitrary(@ForAll("provided") @ShortRange(min = 2, max = 7) short value) {
			return value >= 2 && value <= 7;
		}

		@Provide
		Arbitrary<Short> provided() {
			return Arbitraries
						   .of((short) 0, (short) 1, (short) 2, (short) 3, (short) 4, (short) 5, (short) 7, (short) 8, (short) 10, (short) 100);
		}

	}

	@Group
	class IntRanges {

		@Property
		boolean integers(@ForAll @IntRange(min = 2, max = 7) int value) {
			return value >= 2 && value <= 7;
		}

		@Property
		boolean boxedIntegers(@ForAll @IntRange(min = 2, max = 7) Integer value) {
			return value >= 2 && value <= 7;
		}

		@Property
		boolean integersMinOnly(@ForAll @IntRange(min = 1000000) int value) {
			return value >= 10000 && value <= Integer.MAX_VALUE;
		}

		@Property(generation = GenerationMode.RANDOMIZED)
		boolean alsoWorksForProvidedArbitrary(@ForAll("provided") @IntRange(min = 2, max = 7) int value) {
			return value >= 2 && value <= 7;
		}

		@Provide
		Arbitrary<Integer> provided() {
			return Arbitraries.of(-10, -5, 0, 1, 2, 3, 4, 5, 7, 8, 10, 20, 100);
		}
	}

	@Group
	class LongRanges {

		@Property
		boolean longs(@ForAll @LongRange(min = 2, max = 7) long value) {
			return value >= 2 && value <= 7;
		}

		@Property
		boolean boxedLongs(@ForAll @LongRange(min = 2, max = 7) Long value) {
			return value >= 2 && value <= 7;
		}

		@Property
		boolean longsMinOnly(@ForAll @LongRange(min = Integer.MAX_VALUE) long value) {
			return value >= Integer.MAX_VALUE && value <= Long.MAX_VALUE;
		}

		@Property(generation = GenerationMode.RANDOMIZED)
		boolean alsoWorksForProvidedArbitrary(@ForAll("provided") @LongRange(min = 2, max = 7) long value) {
			return value >= 2 && value <= 7;
		}

		@Provide
		Arbitrary<Long> provided() {
			return Arbitraries.of(0L, 1L, 2L, 3L, 4L, 5L, 7L, 8L, 10L, 100L);
		}

	}

	@Group
	class FloatRanges {

		@Property
		boolean floats(@ForAll @FloatRange(min = 2, max = 7) float value) {
			return value >= 2 && value <= 7;
		}

		@Property
		boolean boxedFloats(@ForAll @FloatRange(min = 2, max = 7) Float value) {
			return value >= 2 && value <= 7;
		}

		@Property
		boolean floatsMinOnly(@ForAll @FloatRange(min = 1000.0f) float value) {
			return value >= 1000.0f && value <= Float.MAX_VALUE;
		}

		@Property
		boolean floatsBordersExcluded(@ForAll @Scale(0) @FloatRange(min = -10.0f, minIncluded = false, max = 10.0f, maxIncluded = false) float value) {
			return value > -10.0f && value < 10.0f;
		}

		@Property
		boolean scaledFloats(@ForAll @FloatRange(min = 2.01f, max = 2.03f) float value) {
			return value >= 2.01f && value <= 2.03f;
		}

		@Property(generation = GenerationMode.RANDOMIZED)
		boolean alsoWorksForProvidedArbitrary(@ForAll("provided") @FloatRange(min = 2, max = 7) float value) {
			return value >= 2 && value <= 7;
		}

		@Provide
		Arbitrary<Float> provided() {
			return Arbitraries.of(0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 7.0f, 8.0f, 10.0f, 100.0f);
		}

	}

	@Group
	class DoubleRanges {

		@Property
		boolean doubles(@ForAll @DoubleRange(min = 2, max = 7) double value) {
			return value >= 2 && value <= 7;
		}

		@Property
		boolean boxedDoubles(@ForAll @DoubleRange(min = 2, max = 7) double value) {
			return value >= 2 && value <= 7;
		}

		@Property
		boolean doublesMinOnly(@ForAll @DoubleRange(min = -100000.0) double value) {
			return value >= -100000 && value <= Double.MAX_VALUE;
		}

		@Property
		boolean doublesBordersExcluded(@ForAll @Scale(0) @DoubleRange(min = -10.0, minIncluded = false, max = 10.0, maxIncluded = false) double value) {
			return value > -10.0 && value < 10.0;
		}

		@Property
		boolean scaledDoubles(@ForAll @DoubleRange(min = 2.01d, max = 2.03d) double value) {
			return value >= 2.01d && value <= 2.03d;
		}

		@Property(generation = GenerationMode.RANDOMIZED)
		boolean alsoWorksForProvidedArbitrary(@ForAll("provided") @DoubleRange(min = 2, max = 7) double value) {
			return value >= 2 && value <= 7;
		}

		@Provide
		Arbitrary<Double> provided() {
			return Arbitraries.of(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 7.0, 8.0, 10.0, 100.0);
		}

	}

	@Group
	class BigRanges {

		@Property
		boolean bigDecimals(@ForAll @BigRange(min = "2.1", max = "7.77") BigDecimal value) {
			return value.compareTo(new BigDecimal("2.1")) >= 0
						   && value.compareTo(new BigDecimal("7.77")) <= 0;
		}

		@Property
		boolean bigDecimalsMinOnly(@ForAll @BigRange(min = "200000.5") BigDecimal value) {
			return value.compareTo(new BigDecimal("200000.5")) >= 0
						   && value.compareTo(new BigDecimal(Double.MAX_VALUE)) <= 0;
		}

		@Property
		boolean bigDecimalsMaxOnly(@ForAll @BigRange(max = "-20") BigDecimal value) {
			return value.compareTo(new BigDecimal("-20")) <= 0
						   && value.compareTo(BigDecimal.valueOf(-Double.MAX_VALUE)) >= 0;
		}

		@Property
		boolean bigDecimalsBordersExcluded(@ForAll @Scale(0) @BigRange(min = "-10", minIncluded = false, max = "10", maxIncluded = false) BigDecimal value) {
			return value.compareTo(new BigDecimal("-10")) > 0
						   && value.compareTo(new BigDecimal("10")) < 0;
		}

		@Property(generation = GenerationMode.RANDOMIZED)
		boolean bigDecimalAlsoWorksForProvidedArbitrary(@ForAll("providedDecimal") @BigRange(min = "2", max = "7") BigDecimal value) {
			return value.compareTo(new BigDecimal("2")) >= 0
						   && value.compareTo(new BigDecimal("7")) <= 0;
		}

		@Provide
		Arbitrary<BigDecimal> providedDecimal() {
			return Arbitraries.of(
					BigDecimal.valueOf(0), BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4),
					BigDecimal.valueOf(5), BigDecimal.valueOf(7), BigDecimal.valueOf(8), BigDecimal.valueOf(10), BigDecimal.valueOf(100)
			);
		}

		@Property
		boolean bigIntegers(@ForAll @BigRange(min = "2.0", max = "7") BigInteger value) {
			return value.compareTo(new BigInteger("2")) >= 0
						   && value.compareTo(new BigInteger("7")) <= 0;
		}

		@Property
		@ExpectFailure
		void bigIntegersWithIncludedFails(@ForAll @BigRange(minIncluded = false, maxIncluded = false) BigInteger value) {
		}

		@Property
		boolean bigIntegersMinOnly(@ForAll @BigRange(min = "1000") BigInteger value) {
			return value.compareTo(new BigInteger("1000")) >= 0
						   && value.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0;
		}

		@Property(generation = GenerationMode.RANDOMIZED)
		boolean bigIntegerAlsoWorksForProvidedArbitrary(@ForAll("providedInteger") @BigRange(min = "2", max = "7") BigInteger value) {
			return value.compareTo(new BigInteger("2")) >= 0
						   && value.compareTo(new BigInteger("7")) <= 0;
		}

		@Provide
		Arbitrary<BigInteger> providedInteger() {
			return Arbitraries.of(
					BigInteger.valueOf(0), BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3),
					BigInteger.valueOf(4), BigInteger.valueOf(5), BigInteger.valueOf(7), BigInteger.valueOf(8),
					BigInteger.valueOf(10), BigInteger.valueOf(100)
			);
		}

	}

}
