package net.jqwik.api.arbitraries;

import java.math.*;

public interface BigIntegerArbitrary extends NullableArbitrary<BigInteger> {

	default BigIntegerArbitrary withRange(BigInteger min, BigInteger max) {
		return withMin(min).withMax(max);
	}

	BigIntegerArbitrary withMin(BigInteger min);

	BigIntegerArbitrary withMax(BigInteger max);
}
