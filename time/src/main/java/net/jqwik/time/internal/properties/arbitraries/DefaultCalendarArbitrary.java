package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.valueRanges.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultCalendarArbitrary extends ArbitraryDecorator<Calendar> implements CalendarArbitrary {

	private LocalDateArbitrary dates = Dates.dates();

	@Override
	protected Arbitrary<Calendar> arbitrary() {
		return dates.map(DefaultCalendarArbitrary::localDateToCalendar);
	}

	@SuppressWarnings("MagicConstant")
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

	@Override
	public CalendarArbitrary atTheEarliest(Calendar min) {
		if (min.get(Calendar.ERA) != GregorianCalendar.AD) {
			throw new IllegalArgumentException("Minimum year in a date must be > 0");
		}
		if (min.get(Calendar.YEAR) > 292_278_993) {
			throw new IllegalArgumentException("Minimum year in a calendar date must be <= 292278993");
		}

		DefaultCalendarArbitrary clone = typedClone();
		clone.dates = clone.dates.atTheEarliest(calendarToLocalDate(min));
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

		DefaultCalendarArbitrary clone = typedClone();
		clone.dates = clone.dates.atTheLatest(calendarToLocalDate(max));
		return clone;
	}

	@Override
	public CalendarArbitrary yearBetween(Year min, Year max) {
		YearBetween yearBetween = (YearBetween) new YearBetween().useInCalendar().set(min, max);
		DefaultCalendarArbitrary clone = typedClone();
		clone.dates = clone.dates.yearBetween(yearBetween.getMin(), yearBetween.getMax());
		return clone;
	}

	@Override
	public CalendarArbitrary monthBetween(Month min, Month max) {
		DefaultCalendarArbitrary clone = typedClone();
		clone.dates = clone.dates.monthBetween(min, max);
		return clone;
	}

	@Override
	public CalendarArbitrary onlyMonths(Month... months) {
		DefaultCalendarArbitrary clone = typedClone();
		clone.dates = clone.dates.onlyMonths(months);
		return clone;
	}

	@Override
	public CalendarArbitrary dayOfMonthBetween(int min, int max) {
		DefaultCalendarArbitrary clone = typedClone();
		clone.dates = clone.dates.dayOfMonthBetween(min, max);
		return clone;
	}

	@Override
	public CalendarArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultCalendarArbitrary clone = typedClone();
		clone.dates = clone.dates.onlyDaysOfWeek(daysOfWeek);
		return clone;
	}

}
