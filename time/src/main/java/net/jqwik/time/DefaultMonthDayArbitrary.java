package net.jqwik.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

public class DefaultMonthDayArbitrary extends ArbitraryDecorator<MonthDay> implements MonthDayArbitrary {

	private MonthDay monthDayMin = MonthDay.of(Month.JANUARY, 1);
	private MonthDay monthDayMax = MonthDay.of(Month.DECEMBER, 31);
	private Month monthMin = Month.JANUARY;
	private Month monthMax = Month.DECEMBER;
	private Month[] allowedMonths = new Month[]{Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER};
	private int dayOfMonthMin = 1;
	private int dayOfMonthMax = 31;

	@Override
	protected Arbitrary<MonthDay> arbitrary() {
		DateArbitrary dates = Dates.dates()
								   .atTheEarliest(LocalDate.of(0, monthDayMin.getMonth(), monthDayMin.getDayOfMonth()))
								   .atTheLatest(LocalDate.of(0, monthDayMax.getMonth(), monthDayMax.getDayOfMonth()))
								   .yearBetween(0, 0)
								   .monthBetween(monthMin, monthMax)
								   .onlyMonths(allowedMonths)
								   .dayOfMonthBetween(dayOfMonthMin, dayOfMonthMax);

		Arbitrary<MonthDay> monthDays = dates.map(v -> MonthDay.of(v.getMonth(), v.getDayOfMonth()));
		monthDays = addAllNeededEndOfMonthEdgeCases(monthDays);
		return monthDays;
	}

	private Arbitrary<MonthDay> addAllNeededEndOfMonthEdgeCases(Arbitrary<MonthDay> monthDays) {

		int[][] endOfMonth = new int[][]{{30, 31}, {28, 29}, {30, 31}, {29, 30}, {30, 31}, {29, 30}, {30, 31}, {30, 31}, {29, 30}, {30, 31}, {29, 30}, {30, 31}};
		int neededMonth = Math.max(monthMin.getValue(), monthDayMin.getMonthValue());
		monthDays = monthDays.edgeCases(monthDayConfig -> monthDayConfig.add(MonthDay.of(neededMonth, endOfMonth[neededMonth - 1][0])));
		monthDays = monthDays.edgeCases(monthDayConfig -> monthDayConfig.add(MonthDay.of(neededMonth, endOfMonth[neededMonth - 1][1])));
		return monthDays;

	}

	@Override
	public MonthDayArbitrary atTheEarliest(MonthDay monthDay) {
		DefaultMonthDayArbitrary clone = typedClone();
		clone.monthDayMin = monthDay;
		return clone;
	}

	@Override
	public MonthDayArbitrary atTheLatest(MonthDay monthDay) {
		DefaultMonthDayArbitrary clone = typedClone();
		clone.monthDayMax = monthDay;
		return clone;
	}

	@Override
	public MonthDayArbitrary monthGreaterOrEqual(Month min) {
		DefaultMonthDayArbitrary clone = typedClone();
		clone.monthMin = min;
		return clone;
	}

	@Override
	public MonthDayArbitrary monthLessOrEqual(Month max) {
		DefaultMonthDayArbitrary clone = typedClone();
		clone.monthMax = max;
		return clone;
	}

	@Override
	public MonthDayArbitrary onlyMonths(Month... months) {
		DefaultMonthDayArbitrary clone = typedClone();
		clone.allowedMonths = months;
		return clone;
	}

	@Override
	public MonthDayArbitrary dayOfMonthGreaterOrEqual(int min) {
		DefaultMonthDayArbitrary clone = typedClone();
		clone.dayOfMonthMin = min;
		return clone;
	}

	@Override
	public MonthDayArbitrary dayOfMonthLessOrEqual(int max) {
		DefaultMonthDayArbitrary clone = typedClone();
		clone.dayOfMonthMax = max;
		return clone;
	}
}
