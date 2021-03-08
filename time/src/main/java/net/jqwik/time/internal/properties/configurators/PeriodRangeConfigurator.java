package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class PeriodRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Period.class);
	}

	public Arbitrary<Period> configure(PeriodArbitrary periodArbitrary, PeriodRange range) {
		Period min = parseIsoPeriod(range.min());
		Period max = parseIsoPeriod(range.max());
		return periodArbitrary.between(min, max);
	}

	private Period parseIsoPeriod(String iso) {
		return Period.parse(iso);
	}

}
