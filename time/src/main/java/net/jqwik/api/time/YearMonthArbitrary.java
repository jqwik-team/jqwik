package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;

public interface YearMonthArbitrary extends Arbitrary<YearMonth> {

	//TODO: Documentation
	default YearMonthArbitrary between(YearMonth startYearMonth, YearMonth endYearMonth){
		return atTheEarliest(startYearMonth).atTheLatest(endYearMonth);
	}
	YearMonthArbitrary atTheEarliest(YearMonth yearMonth);
	YearMonthArbitrary atTheLatest(YearMonth yearMonth);

	default YearMonthArbitrary yearBetween(Year minYear, Year maxYear){
		return yearGreaterOrEqual(minYear).yearLessOrEqual(maxYear);
	}
	default YearMonthArbitrary yearBetween(int minYear, int maxYear){
		return yearBetween(Year.of(minYear), Year.of(maxYear));
	}
	YearMonthArbitrary yearGreaterOrEqual(Year min);
	default YearMonthArbitrary yearGreaterOrEqual(int min){
		return yearGreaterOrEqual(Year.of(min));
	}
	YearMonthArbitrary yearLessOrEqual(Year min);
	default YearMonthArbitrary yearLessOrEqual(int max){
		return yearLessOrEqual(Year.of(max));
	}

	default YearMonthArbitrary monthBetween(Month minMonth, Month maxMonth){
		return monthGreaterOrEqual(minMonth).monthLessOrEqual(maxMonth);
	}
	default YearMonthArbitrary monthBetween(int minMonth, int maxMonth){
		return monthBetween(Month.of(minMonth), Month.of(maxMonth));
	}
	YearMonthArbitrary monthGreaterOrEqual(Month min);
	default YearMonthArbitrary monthGreaterOrEqual(int min){
		return monthGreaterOrEqual(Month.of(min));
	}
	YearMonthArbitrary monthLessOrEqual(Month max);
	default YearMonthArbitrary monthLessOrEqual(int max){
		return monthLessOrEqual(Month.of(max));
	};
	YearMonthArbitrary onlyMonths(Month... months);

}
