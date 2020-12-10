package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;

public interface DateArbitrary extends Arbitrary<LocalDate> {

	default DateArbitrary between(LocalDate dateBegin, LocalDate dateEnd){
		return atTheEarliest(dateBegin).atTheLatest(dateEnd);
	}
	DateArbitrary atTheEarliest(LocalDate date);
	DateArbitrary atTheLatest(LocalDate date);

	DateArbitrary yearGreaterOrEqual(int min);
	DateArbitrary yearLessOrEqual(int max);

	DateArbitrary monthGreaterOrEqual(int min);
	DateArbitrary monthLessOrEqual(int max);

	DateArbitrary dayOfMonthGreaterOrEqual(int min);
	DateArbitrary dayOfMonthLessOrEqual(int max);

	DateArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek);

}
