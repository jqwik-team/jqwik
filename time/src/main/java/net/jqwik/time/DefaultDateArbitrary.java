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

	@Override
	protected Arbitrary<LocalDate> arbitrary() {
		optimizeRuntime();
		Arbitrary<Year> year = generateYears();
		Arbitrary<Month> month = generateMonths();
		Arbitrary<Integer> dayOfMonth = generateDayOfMonths();
		return Combinators.combine(year, month, dayOfMonth)
						  .as(this::generateValidDateFromValues)
						  .filter(v -> v != null && !v.isBefore(dateMin) && !v.isAfter(dateMax));
	}

	private Arbitrary<Year> generateYears(){
		return Dates.years().between(yearMin, yearMax);
	}

	private Arbitrary<Month> generateMonths(){
		return Dates.months().between(monthMin, monthMax).only(allowedMonths);
	}

	private Arbitrary<Integer> generateDayOfMonths(){
		return Dates.daysOfMonth().between(dayOfMonthMin, dayOfMonthMax);
	}

	private LocalDate generateValidDateFromValues(Year y, Month m, int d){
		LocalDate date;
		try {
			date = LocalDate.of(y.getValue(), m, d);
		} catch (DateTimeException e){
			return null;
		}
		return date;
	}

	private void optimizeRuntime(){
		yearMin = Year.of(dateMin.getYear());
		yearMax = Year.of(dateMax.getYear());
		if(yearMin.equals(yearMax)){
			monthMin = dateMin.getMonth();
			monthMax = dateMax.getMonth();
			if(monthMin.equals(monthMax)){
				dayOfMonthMin = dateMin.getDayOfMonth();
				dayOfMonthMax = dateMax.getDayOfMonth();
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
	public DateArbitrary yearGreaterOrEqual(Year min) {
		DefaultDateArbitrary clone = typedClone();
		min = Year.of(Math.max(min.getValue(), LocalDate.MIN.getYear()));
		clone.yearMin = min;
		return clone;
	}

	@Override
	public DateArbitrary yearLessOrEqual(Year max) {
		DefaultDateArbitrary clone = typedClone();
		max = Year.of(Math.min(max.getValue(), LocalDate.MAX.getYear()));
		clone.yearMax = max;
		return clone;
	}

	@Override
	public DateArbitrary monthGreaterOrEqual(Month min) {
		DefaultDateArbitrary clone = typedClone();
		clone.monthMin = min;
		return clone;
	}

	@Override
	public DateArbitrary monthLessOrEqual(Month max) {
		DefaultDateArbitrary clone = typedClone();
		clone.monthMax = max;
		return clone;
	}

	@Override
	public DateArbitrary onlyMonths(Month... months) {
		DefaultDateArbitrary clone = typedClone();
		clone.allowedMonths = months;
		return clone;
	}

	@Override
	public DateArbitrary dayOfMonthGreaterOrEqual(int min) {
		DefaultDateArbitrary clone = typedClone();
		min = Math.max(1, min);
		clone.dayOfMonthMin = min;
		return clone;
	}

	@Override
	public DateArbitrary dayOfMonthLessOrEqual(int max) {
		DefaultDateArbitrary clone = typedClone();
		max = Math.min(31, max);
		clone.dayOfMonthMax = max;
		return clone;
	}

	@Override
	public DateArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultDateArbitrary clone = typedClone();
		return clone;
	}

}
