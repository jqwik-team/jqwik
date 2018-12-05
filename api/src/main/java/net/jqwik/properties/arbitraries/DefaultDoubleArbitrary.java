package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.math.*;

public class DefaultDoubleArbitrary extends AbstractArbitraryBase implements DoubleArbitrary {

	private static final double DEFAULT_MIN = -Double.MAX_VALUE;
	private static final double DEFAULT_MAX = Double.MAX_VALUE;
	private static final int DEFAULT_SCALE = 2;

	private final DecimalGeneratingArbitrary generatingArbitrary;

	public DefaultDoubleArbitrary() {
		this.generatingArbitrary = new DecimalGeneratingArbitrary(toBigDecimal(DEFAULT_MIN), toBigDecimal(DEFAULT_MAX));
	}

	@Override
	public RandomGenerator<Double> generator(int genSize) {
		return generatingArbitrary.generator(genSize).map(BigDecimal::doubleValue);
	}

	@Override
	public DoubleArbitrary greaterOrEqual(double min) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.min = toBigDecimal(min);
		return clone;
	}

	@Override
	public DoubleArbitrary lessOrEqual(double max) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.max = toBigDecimal(max);
		return clone;
	}

	@Override
	public DoubleArbitrary ofScale(int scale) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.scale = scale;
		return clone;
	}

	private BigDecimal toBigDecimal(double value) {
		return BigDecimal.valueOf(value);
	}

}
