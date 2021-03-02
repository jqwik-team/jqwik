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

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, MonthDayRange range) {
		MonthDayArbitrary monthDayArbitrary = (MonthDayArbitrary) arbitrary;
		return monthDayArbitrary.between(isoDateToMonthDay(range.min()), isoDateToMonthDay(range.max()));
	}

	private MonthDay isoDateToMonthDay(String iso) {
		return MonthDay.parse(iso);
	}

}
