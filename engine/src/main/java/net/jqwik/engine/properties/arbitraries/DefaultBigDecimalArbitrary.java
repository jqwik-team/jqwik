package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.*;

public class DefaultBigDecimalArbitrary extends AbstractArbitraryBase implements BigDecimalArbitrary {

	private static final BigDecimal DEFAULT_MIN = new BigDecimal(-Double.MAX_VALUE);
	private static final BigDecimal DEFAULT_MAX = new BigDecimal(Double.MAX_VALUE);
	private static final Range<BigDecimal> DEFAULT_RANGE = Range.of(DEFAULT_MIN, DEFAULT_MAX);

	private final DecimalGeneratingArbitrary generatingArbitrary;

	public DefaultBigDecimalArbitrary() {
		this.generatingArbitrary = new DecimalGeneratingArbitrary(DEFAULT_RANGE);
	}

	@Override
	public RandomGenerator<BigDecimal> generator(int genSize) {
		return generatingArbitrary.generator(genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<BigDecimal>> exhaustive(long maxNumberOfSamples) {
		return generatingArbitrary.exhaustive(maxNumberOfSamples);
	}

	@Override
	public BigDecimalArbitrary greaterOrEqual(BigDecimal min) {
		DefaultBigDecimalArbitrary clone = typedClone();
		clone.generatingArbitrary.range = clone.generatingArbitrary.range.withMin(min != null ? min : DEFAULT_MIN, true);
		return clone;
	}

	@Override
	public BigDecimalArbitrary lessOrEqual(BigDecimal max) {
		DefaultBigDecimalArbitrary clone = typedClone();
		clone.generatingArbitrary.range = clone.generatingArbitrary.range.withMax(max != null ? max : DEFAULT_MAX, true);
		return clone;
	}

	@Override
	public BigDecimalArbitrary ofScale(int scale) {
		DefaultBigDecimalArbitrary clone = typedClone();
		clone.generatingArbitrary.scale = scale;
		return clone;
	}

	@Override
	public BigDecimalArbitrary shrinkTowards(BigDecimal target) {
		DefaultBigDecimalArbitrary clone = typedClone();
		clone.generatingArbitrary.shrinkingTarget = target;
		return clone;
	}

}
