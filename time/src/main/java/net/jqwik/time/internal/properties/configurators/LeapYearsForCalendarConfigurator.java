package net.jqwik.time.internal.properties.configurators;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class LeapYearsForCalendarConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Calendar.class);
	}

	public Arbitrary<?> configure(Arbitrary<Calendar> arbitrary, LeapYears leapYears) {
		boolean withLeapYears = leapYears.withLeapYears();
		if (arbitrary instanceof CalendarArbitrary) {
			CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
			return calendarArbitrary.leapYears(withLeapYears);
		} else {
			return arbitrary.filter(v -> filter(v, withLeapYears));
		}
	}

	private boolean filter(Calendar date, boolean withLeapYears) {
		int year = date.get(Calendar.YEAR);
		if (date.get(Calendar.ERA) == GregorianCalendar.BC) {
			year = -year;
		}
		return withLeapYears || !(new GregorianCalendar().isLeapYear(year));
	}

}
