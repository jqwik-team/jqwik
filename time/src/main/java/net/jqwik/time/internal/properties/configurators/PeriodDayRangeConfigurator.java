package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class PeriodDayRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Period.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, PeriodDayRange range) {
		PeriodArbitrary periodArbitrary = (PeriodArbitrary) arbitrary;
		return periodArbitrary.daysBetween(range.min(), range.max());
	}

}
