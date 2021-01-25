package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.time.format.*;
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
		return targetType.isAssignableFrom(LocalDate.class) || targetType.isAssignableFrom(Calendar.class) || targetType
																													  .isAssignableFrom(Date.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, DateRange range) {
		if (arbitrary instanceof LocalDateArbitrary) {
			LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
			return localDateArbitrary.between(isoDateToLocalDate(range.min()), isoDateToLocalDate(range.max()));
		} else if (arbitrary instanceof CalendarArbitrary) {
			CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
			return calendarArbitrary.between(isoDateToCalendar(range.min()), isoDateToCalendar(range.max()));
		} else if (arbitrary instanceof DateArbitrary) {
			DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
			return dateArbitrary.between(isoDateToDate(range.min()), isoDateToDate(range.max()));
		} else {
			return arbitrary;
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
		if (iso == null) {
			throw new NullPointerException("Argument is null");
		} else if (iso.length() == 0) {
			throw new DateTimeParseException("Date length can not be 0. (Example: 2013-05-25)", iso, 0);
		}
		String[] parts = iso.split("-");
		if (parts.length != 3) {
			throw new DateTimeParseException("Date must consist of three parts. (Example: 2013-05-25)", iso, 0);
		}
		int year, month, day;
		try {
			year = Integer.parseInt(parts[0]);
			month = Integer.parseInt(parts[1]);
			day = Integer.parseInt(parts[2]);
		} catch (NumberFormatException e) {
			throw new DateTimeParseException("Date parts may only consist of digits. (Example: 2013-05-25)", iso, 0);
		}
		return LocalDate.of(year, month, day);
	}

}
