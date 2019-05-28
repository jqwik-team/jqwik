package net.jqwik.engine.properties.arbitraries;

import java.math.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultFloatArbitrary extends AbstractArbitraryBase implements FloatArbitrary {

	private static final float DEFAULT_MIN = -Float.MAX_VALUE;
	private static final float DEFAULT_MAX = Float.MAX_VALUE;

	private final DecimalGeneratingArbitrary generatingArbitrary;

	public DefaultFloatArbitrary() {
		this.generatingArbitrary = new DecimalGeneratingArbitrary(toBigDecimal(DEFAULT_MIN), toBigDecimal(DEFAULT_MAX));
	}

	@Override
	public RandomGenerator<Float> generator(int genSize) {
		return generatingArbitrary.generator(genSize).map(BigDecimal::floatValue);
	}

	@Override
	public FloatArbitrary greaterOrEqual(float min) {
		DefaultFloatArbitrary clone = typedClone();
		clone.generatingArbitrary.min = toBigDecimal(min);
		return clone;
	}

	@Override
	public FloatArbitrary lessOrEqual(float max) {
		DefaultFloatArbitrary clone = typedClone();
		clone.generatingArbitrary.max = toBigDecimal(max);
		return clone;
	}

	@Override
	public FloatArbitrary ofScale(int scale) {
		DefaultFloatArbitrary clone = typedClone();
		clone.generatingArbitrary.scale = scale;
		return clone;
	}

	@Override
	public FloatArbitrary shrinkTowards(float target) {
		DefaultFloatArbitrary clone = typedClone();
		clone.generatingArbitrary.shrinkingTarget = BigDecimal.valueOf(target);
		return clone;
	}

	private BigDecimal toBigDecimal(float value) {
		return new BigDecimal(Float.toString(value));
	}

}
