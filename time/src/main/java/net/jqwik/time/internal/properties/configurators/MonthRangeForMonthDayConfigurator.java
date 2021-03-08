package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class MonthRangeForMonthDayConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(MonthDay.class);
	}

	public Arbitrary<?> configure(Arbitrary<MonthDay> arbitrary, MonthRange range) {
		Month min = range.min();
		Month max = range.max();
		if (arbitrary instanceof MonthDayArbitrary) {
			MonthDayArbitrary monthDayArbitrary = (MonthDayArbitrary) arbitrary;
			return monthDayArbitrary.monthBetween(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private boolean filter(MonthDay monthDay, Month min, Month max) {
		return monthDay.getMonth().compareTo(min) >= 0 && monthDay.getMonth().compareTo(max) <= 0;
	}

}
