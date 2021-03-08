package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

public class MonthRangeForDateConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Date.class);
	}

	public Arbitrary<?> configure(Arbitrary<Date> arbitrary, MonthRange range) {
		Month min = range.min();
		Month max = range.max();
		if (arbitrary instanceof DateArbitrary) {
			DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
			return dateArbitrary.monthBetween(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private boolean filter(Date date, Month min, Month max) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Month month = DefaultCalendarArbitrary.calendarMonthToMonth(calendar);
		return month.compareTo(min) >= 0 && month.compareTo(max) <= 0;
	}

}
