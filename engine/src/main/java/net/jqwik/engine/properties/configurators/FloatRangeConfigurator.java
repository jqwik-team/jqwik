package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class FloatRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Float.class);
	}

	public Arbitrary<Float> configure(Arbitrary<Float> arbitrary, FloatRange range) {
		if (arbitrary instanceof FloatArbitrary) {
			FloatArbitrary floatArbitrary = (FloatArbitrary) arbitrary;
			return floatArbitrary.between(range.min(), range.minIncluded(), range.max(), range.maxIncluded());
		} else {
			return arbitrary.filter(i -> i >= range.min() && i <= range.max());
		}
	}
}
