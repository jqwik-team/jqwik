package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

public class DateRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDate.class)
					   || targetType.isAssignableFrom(Calendar.class)
					   || targetType.isAssignableFrom(Date.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, DateRange range) {
		if (arbitrary instanceof LocalDateArbitrary) {
			LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
			return localDateArbitrary.between(isoDateToLocalDate(range.min()), isoDateToLocalDate(range.max()));
		} else if (arbitrary instanceof CalendarArbitrary) {
			CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
			return calendarArbitrary.between(isoDateToCalendar(range.min()), isoDateToCalendar(range.max()));
		} else {
			DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
			return dateArbitrary.between(isoDateToDate(range.min()), isoDateToDate(range.max()));
		}
	}

	private Calendar isoDateToCalendar(String iso) {
		LocalDate localDate = isoDateToLocalDate(iso);
		return DefaultCalendarArbitrary.localDateToCalendar(localDate);
	}

	private Date isoDateToDate(String iso) {
		Calendar calendar = isoDateToCalendar(iso);
		return calendar.getTime();
	}

	private LocalDate isoDateToLocalDate(String iso) {
		return LocalDate.parse(iso);
	}

}
