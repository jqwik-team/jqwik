package net.jqwik.api.constraints;

import java.math.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;

class RangeProperties {

	@Property
	boolean bytes(@ForAll @ByteRange(min = 2, max = 7) byte value) {
		return value >= 2 && value <= 7;
	}

	@Property
	boolean bytesMinOnly(@ForAll @ByteRange(min = 50) byte value) {
		return value >= 50 && value <= Byte.MAX_VALUE;
	}

	@Property
	boolean shorts(@ForAll @ShortRange(min = 2, max = 7) short value) {
		return value >= 2 && value <= 7;
	}

	@Property
	boolean shortsMinOnly(@ForAll @ShortRange(min = -2000) short value) {
		return value >= -2000 && value <= Short.MAX_VALUE;
	}

	@Property
	boolean integers(@ForAll @IntRange(min = 2, max = 7) int value) {
		return value >= 2 && value <= 7;
	}

	@Property
	boolean integersMinOnly(@ForAll @IntRange(min = 1000000) int value) {
		return value >= 10000 && value <= Integer.MAX_VALUE;
	}

	@Property
	boolean longs(@ForAll @LongRange(min = 2, max = 7) long value) {
		return value >= 2 && value <= 7;
	}

	@Property
	boolean longsMinOnly(@ForAll @LongRange(min = Integer.MAX_VALUE) long value) {
		return value >= Integer.MAX_VALUE && value <= Long.MAX_VALUE;
	}

	@Property
	boolean floats(@ForAll @FloatRange(min = 2, max = 7) float value) {
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

	@Property
	boolean doubles(@ForAll @DoubleRange(min = 2, max = 7) double value) {
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
}
