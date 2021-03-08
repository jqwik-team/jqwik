package net.jqwik.time.internal.properties.configurators;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class DayOfMonthRangeForCalendarConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Calendar.class);
	}

	public Arbitrary<?> configure(Arbitrary<Calendar> arbitrary, DayOfMonthRange range) {
		int min = range.min();
		int max = range.max();
		if (arbitrary instanceof CalendarArbitrary) {
			CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
			return calendarArbitrary.dayOfMonthBetween(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private boolean filter(Calendar date, int min, int max) {
		return date.get(Calendar.DAY_OF_MONTH) >= min && date.get(Calendar.DAY_OF_MONTH) <= max;
	}

}
