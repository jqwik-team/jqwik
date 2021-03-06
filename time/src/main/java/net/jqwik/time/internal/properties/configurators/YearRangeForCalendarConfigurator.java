package net.jqwik.time.internal.properties.configurators;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class YearRangeForCalendarConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Calendar.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, YearRange range) {
		int min = range.min();
		int max = range.max();
		if (arbitrary instanceof CalendarArbitrary) {
			CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
			return calendarArbitrary.yearBetween(min, max);
		} else {
			return arbitrary.filter(v -> filter((Calendar) v, min, max));
		}
	}

	private boolean filter(Calendar date, int min, int max) {
		int year = date.get(Calendar.YEAR);
		if (date.get(Calendar.ERA) == GregorianCalendar.BC) {
			year *= -1;
		}
		return year >= min && year <= max;
	}

}
