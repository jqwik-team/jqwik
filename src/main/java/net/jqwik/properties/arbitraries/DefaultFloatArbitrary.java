package net.jqwik.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultFloatArbitrary extends NullableArbitraryBase<Float> implements FloatArbitrary {

	private static final float DEFAULT_MIN = -Float.MAX_VALUE;
	private static final float DEFAULT_MAX = Float.MAX_VALUE;

	private final DecimalGeneratingArbitrary generatingArbitrary;

	public DefaultFloatArbitrary() {
		super(Float.class);
		this.generatingArbitrary = new DecimalGeneratingArbitrary(toBigDecimal(DEFAULT_MIN), toBigDecimal(DEFAULT_MAX));
	}

	@Override
	protected RandomGenerator<Float> baseGenerator(int tries) {
		return generatingArbitrary.generator(tries).map(BigDecimal::floatValue);
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

	private BigDecimal toBigDecimal(float value) {
		return BigDecimal.valueOf(value);
	}

}
