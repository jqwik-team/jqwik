package net.jqwik.api.arbitraries;

import java.math.*;

import net.jqwik.api.*;

public interface BigDecimalArbitrary extends Arbitrary<BigDecimal> {
	BigDecimalArbitrary withMin(BigDecimal min);

	BigDecimalArbitrary withMax(BigDecimal max);

	BigDecimalArbitrary withScale(int scale);
}
