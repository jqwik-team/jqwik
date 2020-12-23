package net.jqwik.time;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultYearMonthArbitrary extends ArbitraryDecorator<YearMonth> implements YearMonthArbitrary {

	private YearMonth yearMonthMin = YearMonth.of(Year.MIN_VALUE, Month.JANUARY);
	private YearMonth yearMonthMax = YearMonth.of(Year.MAX_VALUE, Month.DECEMBER);
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
		return dates.map(v -> YearMonth.of(v.getYear(), v.getMonth()));
	}

	@Override
	public YearMonthArbitrary atTheEarliest(YearMonth min) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.yearMonthMin = min;
		return clone;
	}

	@Override
	public YearMonthArbitrary atTheLatest(YearMonth max) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.yearMonthMax = max;
		return clone;
	}

	@Override
	public YearMonthArbitrary yearBetween(Year min, Year max) {
		DefaultYearMonthArbitrary clone = typedClone();
		max = Year.of(Math.min(max.getValue(), Year.MAX_VALUE));
		min = Year.of(Math.max(min.getValue(), Year.MIN_VALUE));
		clone.yearMax = max;
		clone.yearMin = min;
		return clone;
	}

	@Override
	public YearMonthArbitrary monthBetween(Month min, Month max) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.monthMin = min;
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
