package net.jqwik.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

public class DefaultDateArbitrary extends ArbitraryDecorator<LocalDate> implements DateArbitrary {

	private LocalDate dateMin = LocalDate.MIN;
	private LocalDate dateMax = LocalDate.MAX;
	private Year yearMin = Year.of(LocalDate.MIN.getYear());
	private Year yearMax = Year.of(LocalDate.MAX.getYear());
	private Month monthMin = Month.JANUARY;
	private Month monthMax = Month.DECEMBER;
	private Month[] allowedMonths = new Month[]{Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER};
	private int dayOfMonthMin = 1;
	private int dayOfMonthMax = 31;
	private DayOfWeek[] allowedDayOfWeeks = new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};

	@Override
	protected Arbitrary<LocalDate> arbitrary() {
		optimizeRuntime();
		Arbitrary<Year> year = generateYears();
		Arbitrary<Month> month = generateMonths();
		Arbitrary<Integer> dayOfMonth = generateDayOfMonths();
		Arbitrary<LocalDate> localDates = Combinators.combine(year, month, dayOfMonth)
													 .as(this::generateDateFromValues)
													 .ignoreException(DateTimeException.class);
		if (!dateMin.equals(LocalDate.MIN)) {
			localDates = localDates.edgeCases(localDateConfig -> localDateConfig.add(dateMin));
		}
		if (!dateMax.equals(LocalDate.MAX)) {
			localDates = localDates.edgeCases(localDateConfig -> localDateConfig.add(dateMax));
		}
		return localDates;
	}

	private Arbitrary<Year> generateYears() {
		return Dates.years().between(yearMin, yearMax);
	}

	private Arbitrary<Month> generateMonths() {
		return Dates.months().between(monthMin, monthMax).only(allowedMonths);
	}

	private Arbitrary<Integer> generateDayOfMonths() {
		return Dates.daysOfMonth().between(dayOfMonthMin, dayOfMonthMax);
	}

	private LocalDate generateDateFromValues(Year y, Month m, int d) {
		LocalDate date;
		date = LocalDate.of(y.getValue(), m, d);
		if (date.isBefore(dateMin) || date.isAfter(dateMax) || !isInAllowedDayOfWeeks(date.getDayOfWeek())) {
			throw new DateTimeException("Invalid date for the input parameters");
		}
		return date;
	}

	private boolean isInAllowedDayOfWeeks(DayOfWeek dayOfWeek) {
		if (allowedDayOfWeeks == null) {
			return false;
		}
		for (DayOfWeek d : allowedDayOfWeeks) {
			if (d.equals(dayOfWeek)) {
				return true;
			}
		}
		return false;
	}

	private void optimizeRuntime() {
		yearMin = Year.of(Math.max(yearMin.getValue(), dateMin.getYear()));
		yearMax = Year.of(Math.min(yearMax.getValue(), dateMax.getYear()));
		if (yearMin.equals(yearMax)) {
			monthMin = Month.of(Math.max(monthMin.getValue(), dateMin.getMonth().getValue()));
			monthMax = Month.of(Math.min(monthMax.getValue(), dateMax.getMonth().getValue()));
			if (monthMin.equals(monthMax)) {
				dayOfMonthMin = Math.max(dayOfMonthMin, dateMin.getDayOfMonth());
				dayOfMonthMax = Math.min(dayOfMonthMax, dateMax.getDayOfMonth());
			}
		}
	}

	@Override
	public DateArbitrary atTheEarliest(LocalDate date) {
		DefaultDateArbitrary clone = typedClone();
		clone.dateMin = date;
		return clone;
	}

	@Override
	public DateArbitrary atTheLatest(LocalDate date) {
		DefaultDateArbitrary clone = typedClone();
		clone.dateMax = date;
		return clone;
	}

	@Override
	public DateArbitrary yearBetween(Year minYear, Year maxYear) {
		DefaultDateArbitrary clone = typedClone();
		minYear = Year.of(Math.max(minYear.getValue(), LocalDate.MIN.getYear()));
		clone.yearMin = minYear;
		maxYear = Year.of(Math.min(maxYear.getValue(), LocalDate.MAX.getYear()));
		clone.yearMax = maxYear;
		return clone;
	}

	@Override
	public DateArbitrary monthBetween(Month minMonth, Month maxMonth) {
		DefaultDateArbitrary clone = typedClone();
		clone.monthMin = minMonth;
		clone.monthMax = maxMonth;
		return clone;
	}

	@Override
	public DateArbitrary onlyMonths(Month... months) {
		DefaultDateArbitrary clone = typedClone();
		clone.allowedMonths = months;
		return clone;
	}

	@Override
	public DateArbitrary dayOfMonthBetween(int minDayOfMonth, int maxDayOfMonth) {
		DefaultDateArbitrary clone = typedClone();
		minDayOfMonth = Math.max(1, minDayOfMonth);
		clone.dayOfMonthMin = minDayOfMonth;
		maxDayOfMonth = Math.min(31, maxDayOfMonth);
		clone.dayOfMonthMax = maxDayOfMonth;
		return clone;
	}

	@Override
	public DateArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultDateArbitrary clone = typedClone();
		clone.allowedDayOfWeeks = daysOfWeek;
		return clone;
	}

}
