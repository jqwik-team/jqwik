package net.jqwik.api.arbitraries;

import java.math.*;

public interface BigIntegerArbitrary extends NullableArbitrary<BigInteger> {

	default BigIntegerArbitrary between(BigInteger min, BigInteger max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	BigIntegerArbitrary greaterOrEqual(BigInteger min);

	BigIntegerArbitrary lessOrEqual(BigInteger max);
}
