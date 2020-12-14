package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;

public interface DateArbitrary extends Arbitrary<LocalDate> {

	//TODO: Documentation
	default DateArbitrary between(LocalDate dateBegin, LocalDate dateEnd) {
		return atTheEarliest(dateBegin).atTheLatest(dateEnd);
	}

	DateArbitrary atTheEarliest(LocalDate date);

	DateArbitrary atTheLatest(LocalDate date);

	default DateArbitrary yearBetween(Year minYear, Year maxYear) {
		return yearGreaterOrEqual(minYear).yearLessOrEqual(maxYear);
	}

	default DateArbitrary yearBetween(int minYear, int maxYear) {
		return yearBetween(Year.of(minYear), Year.of(maxYear));
	}

	DateArbitrary yearGreaterOrEqual(Year min);

	default DateArbitrary yearGreaterOrEqual(int min) {
		return yearGreaterOrEqual(Year.of(min));
	}

	DateArbitrary yearLessOrEqual(Year max);

	default DateArbitrary yearLessOrEqual(int max) {
		return yearLessOrEqual(Year.of(max));
	}

	default DateArbitrary monthBetween(Month minMonth, Month maxMonth) {
		return monthGreaterOrEqual(minMonth).monthLessOrEqual(maxMonth);
	}

	default DateArbitrary monthBetween(int minMonth, int maxMonth) {
		return monthBetween(Month.of(minMonth), Month.of(maxMonth));
	}

	DateArbitrary monthGreaterOrEqual(Month min);

	default DateArbitrary monthGreaterOrEqual(int min) {
		return monthGreaterOrEqual(Month.of(min));
	}

	DateArbitrary monthLessOrEqual(Month max);

	default DateArbitrary monthLessOrEqual(int max) {
		return monthLessOrEqual(Month.of(max));
	}

	;

	DateArbitrary onlyMonths(Month... months);

	default DateArbitrary dayOfMonthBetween(int minDayOfMonth, int maxDayOfMonth) {
		return dayOfMonthGreaterOrEqual(minDayOfMonth).dayOfMonthLessOrEqual(maxDayOfMonth);
	}

	DateArbitrary dayOfMonthGreaterOrEqual(int min);

	DateArbitrary dayOfMonthLessOrEqual(int max);

	DateArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek);

}
