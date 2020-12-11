package net.jqwik.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

public class DefaultYearMonthArbitrary extends ArbitraryDecorator<YearMonth> implements YearMonthArbitrary {

	private YearMonth yearMonthMin = YearMonth.of(Year.MIN_VALUE, 1);
	private YearMonth yearMonthMax = YearMonth.of(Year.MAX_VALUE, 12);
	private Year yearMin = Year.of(Year.MIN_VALUE);
	private Year yearMax = Year.of(Year.MAX_VALUE);
	private Month monthMin = Month.JANUARY;
	private Month monthMax = Month.DECEMBER;
	private Month[] allowedMonths = new Month[]{Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER};


	@Override
	protected Arbitrary<YearMonth> arbitrary() {
		DateArbitrary dates = Dates.dates()
								   .atTheEarliest(LocalDate.of(yearMonthMin.getYear(), yearMonthMin.getMonth(), 1))
								   .atTheLatest(LocalDate.of(yearMonthMax.getYear(), yearMonthMax.getMonth(), 1))
								   .yearBetween(yearMin, yearMax)
								   .monthBetween(monthMin, monthMax)
								   .onlyMonths(allowedMonths)
								   .dayOfMonthBetween(1, 1);
		return dates.map(v -> YearMonth.of(v.getYear(), v.getMonth().getValue()));
	}

	@Override
	public YearMonthArbitrary atTheEarliest(YearMonth yearMonth) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.yearMonthMin = yearMonth;
		return clone;
	}

	@Override
	public YearMonthArbitrary atTheLatest(YearMonth yearMonth) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.yearMonthMax = yearMonth;
		return clone;
	}

	@Override
	public YearMonthArbitrary yearGreaterOrEqual(Year min) {
		DefaultYearMonthArbitrary clone = typedClone();
		min = Year.of(Math.max(min.getValue(), Year.MIN_VALUE));
		clone.yearMin = min;
		return clone;
	}

	@Override
	public YearMonthArbitrary yearLessOrEqual(Year max) {
		DefaultYearMonthArbitrary clone = typedClone();
		max = Year.of(Math.min(max.getValue(), Year.MAX_VALUE));
		clone.yearMax = max;
		return clone;
	}

	@Override
	public YearMonthArbitrary monthGreaterOrEqual(Month min) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.monthMin = min;
		return clone;
	}

	@Override
	public YearMonthArbitrary monthLessOrEqual(Month max) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.monthMax = max;
		return clone;
	}

	@Override
	public YearMonthArbitrary onlyMonths(Month... months) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.allowedMonths = months;
		return clone;
	}

}
