package net.jqwik.api.arbitraries;

import java.math.*;

public interface BigIntegerArbitrary extends NullableArbitrary<BigInteger> {
	BigIntegerArbitrary withMin(BigInteger min);

	BigIntegerArbitrary withMax(BigInteger max);
}
