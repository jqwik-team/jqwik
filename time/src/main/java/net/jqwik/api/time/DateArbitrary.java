package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;

public interface DateArbitrary extends Arbitrary<LocalDate> {

	//TODO: Documentation
	default DateArbitrary between(LocalDate dateBegin, LocalDate dateEnd){
		return atTheEarliest(dateBegin).atTheLatest(dateEnd);
	}
	DateArbitrary atTheEarliest(LocalDate date);
	DateArbitrary atTheLatest(LocalDate date);

	default DateArbitrary yearBetween(int minYear, int maxYear){
		return yearGreaterOrEqual(minYear).yearLessOrEqual(maxYear);
	}
	DateArbitrary yearGreaterOrEqual(int min);
	DateArbitrary yearLessOrEqual(int max);

	default DateArbitrary monthBetween(Month minMonth, Month maxMonth){
		return monthGreaterOrEqual(minMonth).monthLessOrEqual(maxMonth);
	}
	DateArbitrary monthGreaterOrEqual(Month min);
	DateArbitrary monthLessOrEqual(Month max);

	default DateArbitrary dayOfMonthBetween(int minDayOfMonth, int maxDayOfMonth){
		return dayOfMonthGreaterOrEqual(minDayOfMonth).dayOfMonthLessOrEqual(maxDayOfMonth);
	}
	DateArbitrary dayOfMonthGreaterOrEqual(int min);
	DateArbitrary dayOfMonthLessOrEqual(int max);

	DateArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek);

}
