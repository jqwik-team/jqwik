package net.jqwik.api.arbitraries;

import java.math.*;

public interface BigDecimalArbitrary extends NullableArbitrary<BigDecimal> {

	default BigDecimalArbitrary between(BigDecimal min, BigDecimal max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	BigDecimalArbitrary greaterOrEqual(BigDecimal min);

	BigDecimalArbitrary lessOrEqual(BigDecimal max);

	BigDecimalArbitrary ofScale(int scale);
}
