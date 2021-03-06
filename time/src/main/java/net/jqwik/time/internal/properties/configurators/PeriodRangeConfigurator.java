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

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, PeriodRange range) {
		Period min = parseIsoPeriod(range.min());
		Period max = parseIsoPeriod(range.max());
		if (arbitrary instanceof PeriodArbitrary) {
			PeriodArbitrary periodArbitrary = (PeriodArbitrary) arbitrary;
			return periodArbitrary.between(min, max);
		} else {
			return arbitrary.filter(v -> filter((Period) v, min, max));
		}
	}

	private Period parseIsoPeriod(String iso) {
		return Period.parse(iso);
	}

	private boolean filter(Period period, Period min, Period max) {
		return true;
	}

}
