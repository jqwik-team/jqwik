package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;

public interface MonthDayArbitrary extends Arbitrary<MonthDay> {

	//TODO: Documentation
	default MonthDayArbitrary between(MonthDay startMonthDay, MonthDay endMonthDay){
		return atTheEarliest(startMonthDay).atTheLatest(endMonthDay);
	}
	MonthDayArbitrary atTheEarliest(MonthDay monthDay);
	MonthDayArbitrary atTheLatest(MonthDay monthDay);

	default MonthDayArbitrary monthBetween(Month minMonth, Month maxMonth){
		return monthGreaterOrEqual(minMonth).monthLessOrEqual(maxMonth);
	}
	default MonthDayArbitrary monthBetween(int minMonth, int maxMonth){
		return monthBetween(Month.of(minMonth), Month.of(maxMonth));
	}
	MonthDayArbitrary monthGreaterOrEqual(Month min);
	default MonthDayArbitrary monthGreaterOrEqual(int min){
		return monthGreaterOrEqual(Month.of(min));
	}
	MonthDayArbitrary monthLessOrEqual(Month max);
	default MonthDayArbitrary monthLessOrEqual(int max){
		return monthLessOrEqual(Month.of(max));
	};
	MonthDayArbitrary onlyMonths(Month... months);

	default MonthDayArbitrary dayOfMonthBetween(int minDayOfMonth, int maxDayOfMonth){
		return dayOfMonthGreaterOrEqual(minDayOfMonth).dayOfMonthLessOrEqual(maxDayOfMonth);
	}
	MonthDayArbitrary dayOfMonthGreaterOrEqual(int min);
	MonthDayArbitrary dayOfMonthLessOrEqual(int max);

}
