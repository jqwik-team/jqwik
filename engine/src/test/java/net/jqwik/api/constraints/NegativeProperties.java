package net.jqwik.api.constraints;

import java.math.*;

import net.jqwik.api.*;

class NegativeProperties {

	@Property
	boolean negativeBytes(@ForAll @Negative byte value) {
		return value < 0;
	}

	@Property
	boolean negativeShorts(@ForAll @Negative short value) {
		return value < 0;
	}

	@Property
	boolean negativeIntegers(@ForAll @Negative int value) {
		return value < 0;
	}

	@Property
	boolean negativeLongs(@ForAll @Negative long value) {
		return value < 0;
	}

	@Property
	boolean negativeFloats(@ForAll @Negative float value) {
		return value < 0;
	}

	@Property
	boolean negativeDoubles(@ForAll @Negative double value) {
		return value < 0;
	}

	@Property
	boolean negativeBigDecimals(@ForAll @Negative BigDecimal value) {
		return value.compareTo(BigDecimal.ZERO) < 0;
	}

	@Property
	boolean negativeBigIntegers(@ForAll @Negative BigInteger value) {
		return value.compareTo(BigInteger.ZERO) < 0;
	}
}
