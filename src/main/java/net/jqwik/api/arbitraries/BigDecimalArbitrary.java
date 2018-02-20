package net.jqwik.api.arbitraries;

import java.math.*;

public interface BigDecimalArbitrary extends NullableArbitrary<BigDecimal> {
	BigDecimalArbitrary withMin(BigDecimal min);

	BigDecimalArbitrary withMax(BigDecimal max);

	BigDecimalArbitrary withScale(int scale);
}
