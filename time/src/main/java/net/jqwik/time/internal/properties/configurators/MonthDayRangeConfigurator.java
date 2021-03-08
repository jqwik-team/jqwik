package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class MonthDayRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(MonthDay.class);
	}

	public Arbitrary<MonthDay> configure(Arbitrary<MonthDay> arbitrary, MonthDayRange range) {
		MonthDay min = isoDateToMonthDay(range.min());
		MonthDay max = isoDateToMonthDay(range.max());
		if (arbitrary instanceof MonthDayArbitrary) {
			MonthDayArbitrary monthDayArbitrary = (MonthDayArbitrary) arbitrary;
			return monthDayArbitrary.between(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private MonthDay isoDateToMonthDay(String iso) {
		return MonthDay.parse(iso);
	}

	private boolean filter(MonthDay monthDay, MonthDay min, MonthDay max) {
		return !monthDay.isBefore(min) && !monthDay.isAfter(max);
	}

}
