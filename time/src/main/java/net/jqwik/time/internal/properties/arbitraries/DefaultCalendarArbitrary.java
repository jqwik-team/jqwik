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
public class DefaultCalendarArbitrary extends ArbitraryDecorator<Calendar> implements CalendarArbitrary {

	private Calendar calendarMin = null;
	private Calendar calendarMax = null;

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
	protected Arbitrary<Calendar> arbitrary() {

		LocalDateArbitrary dateArbitrary = Dates.dates();

		if (calendarMin != null) {
			dateArbitrary = dateArbitrary.atTheEarliest(calendarToLocalDate(calendarMin));
		}

		if (calendarMax != null) {
			dateArbitrary = dateArbitrary.atTheLatest(calendarToLocalDate(calendarMax));
		}

		if (yearMin != null && yearMax != null) {
			dateArbitrary = dateArbitrary.yearBetween(yearMin, yearMax);
		}

		if (monthMin != null && monthMax != null) {
			dateArbitrary = dateArbitrary.monthBetween(monthMin, monthMax);
		}

		if (onlyMonths != null) {
			dateArbitrary = dateArbitrary.onlyMonths(onlyMonths);
		}

		if (dayOfMonthMin != -1 && dayOfMonthMax != -1) {
			dateArbitrary = dateArbitrary.dayOfMonthBetween(dayOfMonthMin, dayOfMonthMax);
		}

		if (onlyDayOfWeeks != null) {
			dateArbitrary = dateArbitrary.onlyDaysOfWeek(onlyDayOfWeeks);
		}

		if (withLeapYearsSet) {
			dateArbitrary = dateArbitrary.leapYears(withLeapYears);
		}

		return dateArbitrary.map(DefaultCalendarArbitrary::localDateToCalendar);

	}

	@Override
	public CalendarArbitrary atTheEarliest(Calendar min) {
		if (min.get(Calendar.ERA) != GregorianCalendar.AD) {
			throw new IllegalArgumentException("Minimum year in a date must be > 0");
		}
		if (min.get(Calendar.YEAR) > 292_278_993) {
			throw new IllegalArgumentException("Minimum year in a calendar date must be <= 292278993");
		}
		if ((calendarMax != null) && min.after(calendarMax)) {
			throw new IllegalArgumentException("Minimum date must not be after maximum date");
		}

		DefaultCalendarArbitrary clone = typedClone();
		clone.calendarMin = min;
		return clone;
	}

	@Override
	public CalendarArbitrary atTheLatest(Calendar max) {
		if (max.get(Calendar.ERA) != GregorianCalendar.AD) {
			throw new IllegalArgumentException("Maximum year in a date must be > 0");
		}
		if (max.get(Calendar.YEAR) > 292_278_993) {
			throw new IllegalArgumentException("Maximum year in a calendar date must be <= 292278993");
		}
		if ((calendarMin != null) && max.before(calendarMin)) {
			throw new IllegalArgumentException("Maximum date must not be before minimum date");
		}

		DefaultCalendarArbitrary clone = typedClone();
		clone.calendarMax = max;
		return clone;
	}

	@Override
	public CalendarArbitrary yearBetween(Year min, Year max) {
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

		DefaultCalendarArbitrary clone = typedClone();
		clone.yearMin = min;
		clone.yearMax = max;
		return clone;
	}

	@Override
	public CalendarArbitrary monthBetween(Month min, Month max) {
		if (min.compareTo(max) > 0) {
			throw new IllegalArgumentException("Minimum month cannot be after maximum month");
		}

		DefaultCalendarArbitrary clone = typedClone();
		clone.monthMin = min;
		clone.monthMax = max;
		return clone;
	}

	@Override
	public CalendarArbitrary onlyMonths(Month... months) {
		DefaultCalendarArbitrary clone = typedClone();
		clone.onlyMonths = months;
		return clone;
	}

	@Override
	public CalendarArbitrary dayOfMonthBetween(int min, int max) {
		DefaultCalendarArbitrary clone = typedClone();
		clone.dayOfMonthMin = min;
		clone.dayOfMonthMax = max;
		return clone;
	}

	@Override
	public CalendarArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultCalendarArbitrary clone = typedClone();
		clone.onlyDayOfWeeks = daysOfWeek;
		return clone;
	}

	@Override
	public CalendarArbitrary leapYears(boolean withLeapYears) {
		DefaultCalendarArbitrary clone = typedClone();
		clone.withLeapYearsSet = true;
		clone.withLeapYears = withLeapYears;
		return clone;
	}

	public static Calendar localDateToCalendar(LocalDate date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(date.getYear(), monthToCalendarMonth(date.getMonth()), date.getDayOfMonth(), 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	private LocalDate calendarToLocalDate(Calendar date) {
		return LocalDate.of(date.get(Calendar.YEAR), calendarMonthToMonth(date.get(Calendar.MONTH)), date.get(Calendar.DAY_OF_MONTH));
	}

	public static int monthToCalendarMonth(Month month) {
		switch (month) {
			case JANUARY:
				return Calendar.JANUARY;
			case FEBRUARY:
				return Calendar.FEBRUARY;
			case MARCH:
				return Calendar.MARCH;
			case APRIL:
				return Calendar.APRIL;
			case MAY:
				return Calendar.MAY;
			case JUNE:
				return Calendar.JUNE;
			case JULY:
				return Calendar.JULY;
			case AUGUST:
				return Calendar.AUGUST;
			case SEPTEMBER:
				return Calendar.SEPTEMBER;
			case OCTOBER:
				return Calendar.OCTOBER;
			case NOVEMBER:
				return Calendar.NOVEMBER;
			default:
				return Calendar.DECEMBER;
		}
	}

	public static Month calendarMonthToMonth(Calendar calendar) {
		return calendarMonthToMonth(calendar.get(Calendar.MONTH));
	}

	public static Month calendarMonthToMonth(int month) {
		switch (month) {
			case Calendar.JANUARY:
				return Month.JANUARY;
			case Calendar.FEBRUARY:
				return Month.FEBRUARY;
			case Calendar.MARCH:
				return Month.MARCH;
			case Calendar.APRIL:
				return Month.APRIL;
			case Calendar.MAY:
				return Month.MAY;
			case Calendar.JUNE:
				return Month.JUNE;
			case Calendar.JULY:
				return Month.JULY;
			case Calendar.AUGUST:
				return Month.AUGUST;
			case Calendar.SEPTEMBER:
				return Month.SEPTEMBER;
			case Calendar.OCTOBER:
				return Month.OCTOBER;
			case Calendar.NOVEMBER:
				return Month.NOVEMBER;
			default:
				return Month.DECEMBER;
		}
	}

	public static DayOfWeek calendarDayOfWeekToDayOfWeek(Calendar calendar) {
		return calendarDayOfWeekToDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
	}

	public static DayOfWeek calendarDayOfWeekToDayOfWeek(int dayOfWeek) {
		switch (dayOfWeek) {
			case Calendar.MONDAY:
				return DayOfWeek.MONDAY;
			case Calendar.TUESDAY:
				return DayOfWeek.TUESDAY;
			case Calendar.WEDNESDAY:
				return DayOfWeek.WEDNESDAY;
			case Calendar.THURSDAY:
				return DayOfWeek.THURSDAY;
			case Calendar.FRIDAY:
				return DayOfWeek.FRIDAY;
			case Calendar.SATURDAY:
				return DayOfWeek.SATURDAY;
			default:
				return DayOfWeek.SUNDAY;
		}
	}

}
