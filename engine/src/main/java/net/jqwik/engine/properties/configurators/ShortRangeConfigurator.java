package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class ShortRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Short.class);
	}

	public Arbitrary<Short> configure(Arbitrary<Short> arbitrary, ShortRange range) {
		if (arbitrary instanceof ShortArbitrary) {
			ShortArbitrary shortArbitrary = (ShortArbitrary) arbitrary;
			return shortArbitrary.greaterOrEqual(range.min()).lessOrEqual(range.max());
		} else {
			return arbitrary.filter(i -> i >= range.min() && i <= range.max());
		}
	}
}
