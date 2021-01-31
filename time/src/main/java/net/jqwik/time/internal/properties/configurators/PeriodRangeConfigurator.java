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

	public Arbitrary<?> configure(PeriodArbitrary arbitrary, PeriodRange range) {
		Period minPeriod = parseIsoPeriod(range.min());
		Period maxPeriod = parseIsoPeriod(range.max());
		return arbitrary.between(minPeriod, maxPeriod);
	}

	private Period parseIsoPeriod(String iso) {
		return Period.parse(iso);
	}
}
