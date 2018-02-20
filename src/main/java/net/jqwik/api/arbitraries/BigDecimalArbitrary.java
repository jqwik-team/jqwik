package net.jqwik.api.arbitraries;

import java.math.*;

public interface BigDecimalArbitrary extends NullableArbitrary<BigDecimal> {

	default BigDecimalArbitrary withRange(BigDecimal min, BigDecimal max) {
		return withMin(min).withMax(max);
	}

	BigDecimalArbitrary withMin(BigDecimal min);

	BigDecimalArbitrary withMax(BigDecimal max);

	BigDecimalArbitrary withScale(int scale);
}
