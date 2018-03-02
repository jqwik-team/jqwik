package net.jqwik.api.constraints;

import java.math.*;

import net.jqwik.api.*;

class RangeProperties {

	@Property
	boolean bytes(@ForAll @ByteRange(min = 2, max = 7) byte value) {
		return value >= 2 && value <= 7;
	}

	@Property
	boolean shorts(@ForAll @ShortRange(min = 2, max = 7) short value) {
		return value >= 2 && value <= 7;
	}

	@Property
	boolean integers(@ForAll @IntRange(min = 2, max = 7) int value) {
		return value >= 2 && value <= 7;
	}

	@Property
	boolean longs(@ForAll @LongRange(min = 2, max = 7) long value) {
		return value >= 2 && value <= 7;
	}

	@Property
	boolean floats(@ForAll @FloatRange(min = 2, max = 7) float value) {
		return value >= 2 && value <= 7;
	}

	@Property
	boolean doubles(@ForAll @DoubleRange(min = 2, max = 7) double value) {
		return value >= 2 && value <= 7;
	}

	@Property
	boolean bigDecimals(@ForAll @BigRange(min = "2.1", max = "7.77") BigDecimal value) {
		return value.compareTo(new BigDecimal("2.1")) >= 0 //
			&& value.compareTo(new BigDecimal("7.77")) <= 0;
	}

	@Property
	boolean bigIntegers(@ForAll @BigRange(min = "2.0", max = "7") BigInteger value) {
		return value.compareTo(new BigInteger("2")) >= 0 //
			&& value.compareTo(new BigInteger("7")) <= 0;
	}
}
