package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

import java.math.*;

public interface BigIntegerArbitrary extends Arbitrary<BigInteger> {
	BigIntegerArbitrary withMin(BigInteger min);

	BigIntegerArbitrary withMax(BigInteger max);
}
