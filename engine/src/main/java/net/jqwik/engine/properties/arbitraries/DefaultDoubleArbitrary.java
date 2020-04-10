package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.*;

public class DefaultDoubleArbitrary extends AbstractArbitraryBase implements DoubleArbitrary {

	private static final double DEFAULT_MIN = -Double.MAX_VALUE;
	private static final double DEFAULT_MAX = Double.MAX_VALUE;

	private final DecimalGeneratingArbitrary generatingArbitrary;

	public DefaultDoubleArbitrary() {
		this.generatingArbitrary = new DecimalGeneratingArbitrary(Range.of(toBigDecimal(DEFAULT_MIN), toBigDecimal(DEFAULT_MAX)));
	}

	@Override
	public RandomGenerator<Double> generator(int genSize) {
		return generatingArbitrary.generator(genSize).map(BigDecimal::doubleValue);
	}

	@Override
	public Optional<ExhaustiveGenerator<Double>> exhaustive(long maxNumberOfSamples) {
		return generatingArbitrary.exhaustive(maxNumberOfSamples).map(generator -> generator.map(BigDecimal::doubleValue));
	}

	@Override
	public DoubleArbitrary greaterOrEqual(double min) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.range = clone.generatingArbitrary.range.withMin(toBigDecimal(min), true);
		return clone;
	}

	@Override
	public DoubleArbitrary lessOrEqual(double max) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.range = clone.generatingArbitrary.range.withMax(toBigDecimal(max), true);
		return clone;
	}

	@Override
	public DoubleArbitrary ofScale(int scale) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.scale = scale;
		return clone;
	}

	@Override
	public DoubleArbitrary shrinkTowards(double target) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.shrinkingTarget = BigDecimal.valueOf(target);
		return clone;
	}

	private BigDecimal toBigDecimal(double value) {
		return new BigDecimal(Double.toString(value));
	}
}
