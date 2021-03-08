package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

public class MonthRangeForCalendarConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Calendar.class);
	}

	public Arbitrary<?> configure(Arbitrary<Calendar> arbitrary, MonthRange range) {
		Month min = range.min();
		Month max = range.max();
		if (arbitrary instanceof CalendarArbitrary) {
			CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
			return calendarArbitrary.monthBetween(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private boolean filter(Calendar date, Month min, Month max) {
		Month month = DefaultCalendarArbitrary.calendarMonthToMonth(date);
		return month.compareTo(min) >= 0 && month.compareTo(max) <= 0;
	}

}
