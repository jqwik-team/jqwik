package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.math.*;

public class DefaultBigDecimalArbitrary extends AbstractArbitraryBase implements BigDecimalArbitrary {

	private static final BigDecimal DEFAULT_MIN = new BigDecimal(-Double.MAX_VALUE);
	private static final BigDecimal DEFAULT_MAX = new BigDecimal(Double.MAX_VALUE);


	private final DecimalGeneratingArbitrary generatingArbitrary;

	public DefaultBigDecimalArbitrary() {
		this.generatingArbitrary = new DecimalGeneratingArbitrary(DEFAULT_MIN, DEFAULT_MAX);
	}

	@Override
	public RandomGenerator<BigDecimal> generator(int tries) {
		return generatingArbitrary.generator(tries);
	}

	@Override
	public BigDecimalArbitrary greaterOrEqual(BigDecimal min) {
		DefaultBigDecimalArbitrary clone = typedClone();
		clone.generatingArbitrary.min = (min != null ? min : DEFAULT_MIN);
		return clone;
	}

	@Override
	public BigDecimalArbitrary lessOrEqual(BigDecimal max) {
		DefaultBigDecimalArbitrary clone = typedClone();
		clone.generatingArbitrary.max = (max != null ? max : DEFAULT_MAX);
		return clone;
	}

	@Override
	public BigDecimalArbitrary ofScale(int scale) {
		DefaultBigDecimalArbitrary clone = typedClone();
		clone.generatingArbitrary.scale = scale;
		return clone;
	}

}
