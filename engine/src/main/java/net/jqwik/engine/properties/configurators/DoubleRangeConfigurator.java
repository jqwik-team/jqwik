package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class DoubleRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Double.class);
	}

	public Arbitrary<Double> configure(Arbitrary<Double> arbitrary, DoubleRange range) {
		if (arbitrary instanceof DoubleArbitrary) {
			DoubleArbitrary doubleArbitrary = (DoubleArbitrary) arbitrary;
			return doubleArbitrary.between(range.min(), range.minIncluded(), range.max(), range.maxIncluded());
		} else {
			return arbitrary.filter(i -> i >= range.min() && i <= range.max());
		}
	}
}
