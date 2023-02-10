package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultDateArbitrary extends ArbitraryDecorator<Date> implements DateArbitrary {

	private CalendarArbitrary calendars = Dates.datesAsCalendar();

	@Override
	protected Arbitrary<Date> arbitrary() {
		return calendars.map(Calendar::getTime);
	}

	public static Calendar dateToCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	@Override
	public DateArbitrary atTheEarliest(Date min) {
		DefaultDateArbitrary clone = typedClone();
		clone.calendars = clone.calendars.atTheEarliest(dateToCalendar(min));
		return clone;
	}

	@Override
	public DateArbitrary atTheLatest(Date max) {
		DefaultDateArbitrary clone = typedClone();
		clone.calendars = clone.calendars.atTheLatest(dateToCalendar(max));
		return clone;
	}

	@Override
	public DateArbitrary yearBetween(Year min, Year max) {
		DefaultDateArbitrary clone = typedClone();
		clone.calendars = clone.calendars.yearBetween(min, max);
		return clone;
	}

	@Override
	public DateArbitrary monthBetween(Month min, Month max) {
		DefaultDateArbitrary clone = typedClone();
		clone.calendars = clone.calendars.monthBetween(min, max);
		return clone;
	}

	@Override
	public DateArbitrary onlyMonths(Month... months) {
		DefaultDateArbitrary clone = typedClone();
		clone.calendars = clone.calendars.onlyMonths(months);
		return clone;
	}

	@Override
	public DateArbitrary dayOfMonthBetween(int min, int max) {
		DefaultDateArbitrary clone = typedClone();
		clone.calendars = clone.calendars.dayOfMonthBetween(min, max);
		return clone;
	}

	@Override
	public DateArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultDateArbitrary clone = typedClone();
		clone.calendars = clone.calendars.onlyDaysOfWeek(daysOfWeek);
		return clone;
	}

}
