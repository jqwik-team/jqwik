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

	DateArbitrary yearBetween(Year minYear, Year maxYear);

	default DateArbitrary yearBetween(int minYear, int maxYear) {
		return yearBetween(Year.of(minYear), Year.of(maxYear));
	}

	DateArbitrary monthBetween(Month minMonth, Month maxMonth);

	default DateArbitrary monthBetween(int minMonth, int maxMonth) {
		return monthBetween(Month.of(minMonth), Month.of(maxMonth));
	}

	DateArbitrary onlyMonths(Month... months);

	DateArbitrary dayOfMonthBetween(int minDayOfMonth, int maxDayOfMonth);

	DateArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek);

}
