package net.jqwik.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultBigDecimalArbitrary extends NullableArbitraryBase<BigDecimal> implements BigDecimalArbitrary {

	private static final BigDecimal DEFAULT_MIN = new BigDecimal(-1_000_000_000);
	private static final BigDecimal DEFAULT_MAX = new BigDecimal(1_000_000_000);


	private final DecimalGeneratingArbitrary generatingArbitrary;

	public DefaultBigDecimalArbitrary() {
		super(BigDecimal.class);
		this.generatingArbitrary = new DecimalGeneratingArbitrary(DEFAULT_MIN, DEFAULT_MAX);
	}

	@Override
	protected RandomGenerator<BigDecimal> baseGenerator(int tries) {
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
