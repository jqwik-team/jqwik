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
	public EdgeCases<Double> edgeCases() {
		return EdgeCasesSupport.map(generatingArbitrary.edgeCases(), BigDecimal::doubleValue);
	}

	@Override
	public DoubleArbitrary withDistribution(final RandomDistribution distribution) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.distribution = distribution;
		return clone;
	}

	@Override
	public DoubleArbitrary between(double min, boolean minIncluded, double max, boolean maxIncluded) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.range = Range.of(toBigDecimal(min), minIncluded, toBigDecimal(max), maxIncluded);
		return clone;
	}

	@Override
	public DoubleArbitrary greaterOrEqual(double min) {
		return between(min, true, generatingArbitrary.range.max.doubleValue(), generatingArbitrary.range.maxIncluded);
	}

	@Override
	public DoubleArbitrary greaterThan(double min) {
		return between(min, false, generatingArbitrary.range.max.doubleValue(), generatingArbitrary.range.maxIncluded);
	}

	@Override
	public DoubleArbitrary lessOrEqual(double max) {
		return between(generatingArbitrary.range.min.doubleValue(), generatingArbitrary.range.minIncluded, max, true);
	}

	@Override
	public DoubleArbitrary lessThan(double max) {
		return between(generatingArbitrary.range.min.doubleValue(), generatingArbitrary.range.minIncluded, max, false);
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
