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
	private Year yearMin = Year.of(1900);
	private Year yearMax = Year.of(2500);
	private Month monthMin = Month.JANUARY;
	private Month monthMax = Month.DECEMBER;
	private Month[] allowedMonths = new Month[]{Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER};

	@Override
	protected Arbitrary<YearMonth> arbitrary() {
		setYearMinMax();
		DateArbitrary dates = Dates.dates()
								   .atTheEarliest(LocalDate.of(yearMonthMin.getYear(), yearMonthMin.getMonth(), 1))
								   .atTheLatest(LocalDate.of(yearMonthMax.getYear(), yearMonthMax
																							 .getMonth(), getMaxDayOfMonth(yearMonthMax)))
								   .yearBetween(yearMin, yearMax)
								   .monthBetween(monthMin, monthMax)
								   .onlyMonths(allowedMonths)
								   .dayOfMonthBetween(1, 1);
		return dates.map(v -> YearMonth.of(v.getYear(), v.getMonth()));
	}

	private int getMaxDayOfMonth(YearMonth yearMonth) {
		switch (yearMonth.getMonth()) {
			case FEBRUARY:
				return calculateMaxDayOfMonthForFebruary(yearMonth);
			case APRIL:
			case JUNE:
			case SEPTEMBER:
			case NOVEMBER:
				return 30;
			default:
				return 31;
		}
	}

	private int calculateMaxDayOfMonthForFebruary(YearMonth yearMonth) {
		try {
			LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 29);
		} catch (DateTimeException e) {
			return 28;
		}
		return 29;
	}

	private void setYearMinMax() {
		if (yearMin.getValue() == 1900 && !yearMonthMin.equals(YearMonth.of(Year.MIN_VALUE, Month.JANUARY))) {
			yearMin = Year.of(yearMonthMin.getYear());
		}
		if (yearMax.getValue() == 2500 && !yearMonthMax.equals(YearMonth.of(Year.MAX_VALUE, Month.DECEMBER))) {
			yearMax = Year.of(yearMonthMax.getYear());
		}
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
		if (min.getValue() <= 0) {
			throw new IllegalArgumentException("Minimum year in a YearMonth must be > 0");
		}
		if (max.getValue() <= 0) {
			throw new IllegalArgumentException("Maximum year in a YearMonth must be > 0");
		}
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
