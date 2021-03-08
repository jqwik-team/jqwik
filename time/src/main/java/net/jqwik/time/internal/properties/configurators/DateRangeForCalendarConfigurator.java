package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

public class DateRangeForCalendarConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Calendar.class);
	}

	public Arbitrary<?> configure(Arbitrary<Calendar> arbitrary, DateRange range) {
		Calendar min = isoDateToCalendar(range.min(), false);
		Calendar max = isoDateToCalendar(range.max(), true);
		if (arbitrary instanceof CalendarArbitrary) {
			CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
			return calendarArbitrary.between(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private boolean filter(Calendar date, Calendar min, Calendar max) {
		return date.compareTo(min) >= 0 && date.compareTo(max) <= 0;
	}

	public static Calendar isoDateToCalendar(String iso, boolean max) {
		LocalDate localDate = DateRangeForLocalDateConfigurator.isoDateToLocalDate(iso);
		Calendar calendar = DefaultCalendarArbitrary.localDateToCalendar(localDate);
		if (max) {
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
		}
		return calendar;
	}

}
