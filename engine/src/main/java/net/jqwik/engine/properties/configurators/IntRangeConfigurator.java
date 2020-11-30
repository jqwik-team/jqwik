package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class IntRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Integer.class);
	}

	public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, IntRange range) {
		if (arbitrary instanceof IntegerArbitrary) {
			IntegerArbitrary integerArbitrary = (IntegerArbitrary) arbitrary;
			return integerArbitrary.greaterOrEqual(range.min()).lessOrEqual(range.max());
		} else {
			return arbitrary.filter(i -> i >= range.min() && i <= range.max());
		}
	}
}
