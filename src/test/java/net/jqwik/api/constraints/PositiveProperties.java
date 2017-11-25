package net.jqwik.api.constraints;

import java.math.*;

import net.jqwik.api.*;

class PositiveProperties {

	@Property
	void positiveIntegers(@ForAll @Positive int anInt) {
	}

	@Property
	void positiveLongs(@ForAll @Positive long aLong) {
	}

	@Property
	void positiveFloats(@ForAll @Positive float aFloat) {
	}

	@Property
	void positiveDoubles(@ForAll @Positive double aDouble) {
	}

	@Property
	void positiveBigDecimals(@ForAll @Positive BigDecimal aBigDecimal) {
	}

	@Property
	void positiveBigIntegers(@ForAll @Positive BigInteger aBigInteger) {
	}
}
