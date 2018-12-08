package net.jqwik.api.constraints;

import java.math.*;

import net.jqwik.api.*;

class PositiveProperties {

	@Property
	boolean positiveBytes(@ForAll @Positive byte value) {
		return value >= 0;
	}

	@Property
	boolean positiveShorts(@ForAll @Positive short value) {
		return value >= 0;
	}

	@Property
	boolean positiveIntegers(@ForAll @Positive int value) {
		return value >= 0;
	}

	@Property
	boolean positiveLongs(@ForAll @Positive long value) {
		return value >= 0;
	}

	@Property
	boolean positiveFloats(@ForAll @Positive float value) {
		return value >= 0;
	}

	@Property
	boolean positiveDoubles(@ForAll @Positive double value) {
		return value >= 0;
	}

	@Property
	boolean positiveBigDecimals(@ForAll @Positive BigDecimal value) {
		return value.compareTo(BigDecimal.ZERO) >= 0;
	}

	@Property
	boolean positiveBigIntegers(@ForAll @Positive BigInteger value) {
		return value.compareTo(BigInteger.ZERO) >= 0;
	}
}
