package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class LongRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Long.class);
	}

	public Arbitrary<Long> configure(Arbitrary<Long> arbitrary, LongRange range) {
		if (arbitrary instanceof LongArbitrary) {
			LongArbitrary longArbitrary = (LongArbitrary) arbitrary;
			return longArbitrary.greaterOrEqual(range.min()).lessOrEqual(range.max());
		} else {
			return arbitrary.filter(i -> i >= range.min() && i <= range.max());
		}
	}
}
