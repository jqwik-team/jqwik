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

	private Date dateMin = null;
	private Date dateMax = null;

	private Year yearMin = null;
	private Year yearMax = null;

	private Month monthMin = null;
	private Month monthMax = null;
	private Month[] onlyMonths = null;

	private int dayOfMonthMin = -1;
	private int dayOfMonthMax = -1;

	private DayOfWeek[] onlyDayOfWeeks = null;

	private boolean withLeapYearsSet = false;
	private boolean withLeapYears = false;

	@Override
	protected Arbitrary<Date> arbitrary() {

		CalendarArbitrary calendarArbitrary = Dates.datesAsCalendar();

		if (dateMin != null) {
			calendarArbitrary = calendarArbitrary.atTheEarliest(dateToCalendar(dateMin));
		}

		if (dateMax != null) {
			calendarArbitrary = calendarArbitrary.atTheLatest(dateToCalendar(dateMax));
		}

		if (yearMin != null && yearMax != null) {
			calendarArbitrary = calendarArbitrary.yearBetween(yearMin, yearMax);
		}

		if (monthMin != null && monthMax != null) {
			calendarArbitrary = calendarArbitrary.monthBetween(monthMin, monthMax);
		}

		if (onlyMonths != null) {
			calendarArbitrary = calendarArbitrary.onlyMonths(onlyMonths);
		}

		if (dayOfMonthMin != -1 && dayOfMonthMax != -1) {
			calendarArbitrary = calendarArbitrary.dayOfMonthBetween(dayOfMonthMin, dayOfMonthMax);
		}

		if (onlyDayOfWeeks != null) {
			calendarArbitrary = calendarArbitrary.onlyDaysOfWeek(onlyDayOfWeeks);
		}

		if (withLeapYearsSet) {
			calendarArbitrary = calendarArbitrary.leapYears(withLeapYears);
		}

		return calendarArbitrary.map(Calendar::getTime);

	}

	@Override
	public DateArbitrary atTheEarliest(Date min) {
		Calendar calendar = dateToCalendar(min);
		if (calendar.get(Calendar.YEAR) <= 0) {
			throw new IllegalArgumentException("Minimum year in a date must be > 0");
		}
		if (calendar.get(Calendar.YEAR) > 292_278_993) {
			throw new IllegalArgumentException("Minimum year in a calendar date must be <= 292278993");
		}
		if ((dateMax != null) && min.after(dateMax)) {
			throw new IllegalArgumentException("Minimum date must not be after maximum date");
		}

		DefaultDateArbitrary clone = typedClone();
		clone.dateMin = min;
		return clone;
	}

	@Override
	public DateArbitrary atTheLatest(Date max) {
		Calendar calendar = dateToCalendar(max);
		if (calendar.get(Calendar.YEAR) <= 0) {
			throw new IllegalArgumentException("Maximum year in a date must be > 0");
		}
		if (calendar.get(Calendar.YEAR) > 292_278_993) {
			throw new IllegalArgumentException("Maximum year in a calendar date must be <= 292278993");
		}
		if ((dateMin != null) && max.before(dateMin)) {
			System.out.println(dateMin + ", " + max);
			throw new IllegalArgumentException("Maximum date must not be before minimum date");
		}

		DefaultDateArbitrary clone = typedClone();
		clone.dateMax = max;
		return clone;
	}

	@Override
	public DateArbitrary yearBetween(Year min, Year max) {
		if (min.getValue() <= 0) {
			throw new IllegalArgumentException("Minimum year in a date must be > 0");
		}
		if (max.getValue() <= 0) {
			throw new IllegalArgumentException("Maximum year in a date must be > 0");
		}
		if (min.getValue() > 292_278_993) {
			throw new IllegalArgumentException("Minimum year in a calendar date must be <= 292278993");
		}
		if (max.getValue() > 292_278_993) {
			throw new IllegalArgumentException("Maximum year in a calendar date must be <= 292278993");
		}

		DefaultDateArbitrary clone = typedClone();
		clone.yearMin = min;
		clone.yearMax = max;
		return clone;
	}

	@Override
	public DateArbitrary monthBetween(Month min, Month max) {
		if (min.compareTo(max) > 0) {
			throw new IllegalArgumentException("Minimum month cannot be after maximum month");
		}

		DefaultDateArbitrary clone = typedClone();
		clone.monthMin = min;
		clone.monthMax = max;
		return clone;
	}

	@Override
	public DateArbitrary onlyMonths(Month... months) {
		DefaultDateArbitrary clone = typedClone();
		clone.onlyMonths = months;
		return clone;
	}

	@Override
	public DateArbitrary dayOfMonthBetween(int min, int max) {
		DefaultDateArbitrary clone = typedClone();
		clone.dayOfMonthMin = min;
		clone.dayOfMonthMax = max;
		return clone;
	}

	@Override
	public DateArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultDateArbitrary clone = typedClone();
		clone.onlyDayOfWeeks = daysOfWeek;
		return clone;
	}

	@Override
	public DateArbitrary leapYears(boolean withLeapYears) {
		DefaultDateArbitrary clone = typedClone();
		clone.withLeapYearsSet = true;
		clone.withLeapYears = withLeapYears;
		return clone;
	}

	private Calendar dateToCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

}
