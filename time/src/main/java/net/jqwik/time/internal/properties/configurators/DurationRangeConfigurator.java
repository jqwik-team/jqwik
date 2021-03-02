package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class DurationRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Duration.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, DurationRange range) {
		DurationArbitrary durationArbitrary = (DurationArbitrary) arbitrary;
		return durationArbitrary.between(Duration.parse(range.min()), Duration.parse(range.max()));
	}

}
