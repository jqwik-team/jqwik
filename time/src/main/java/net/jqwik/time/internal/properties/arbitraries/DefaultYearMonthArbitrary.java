package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.valueRanges.*;

import static java.time.temporal.ChronoUnit.*;
import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultYearMonthArbitrary extends ArbitraryDecorator<YearMonth> implements YearMonthArbitrary {

	private static final YearMonth DEFAULT_MIN = YearMonth.of(1900, 1);
	private static final YearMonth DEFAULT_MAX = YearMonth.of(2500, 12);

	private final YearMonthBetween yearMonthBetween = new YearMonthBetween();
	private final AllowedMonths allowedMonths = new AllowedMonths();
	private final WithLeapYears withLeapYears = new WithLeapYears();

	@Override
	protected Arbitrary<YearMonth> arbitrary() {

		YearMonth effectiveMin = yearMonthBetween.getMin() == null ? DEFAULT_MIN : yearMonthBetween.getMin();
		YearMonth effectiveMax = yearMonthBetween.getMax() == null ? DEFAULT_MAX : yearMonthBetween.getMax();

		long months = MONTHS.between(effectiveMin, effectiveMax);

		Arbitrary<Long> month =
			Arbitraries.longs()
					   .between(0, months)
					   .withDistribution(RandomDistribution.uniform())
					   .edgeCases(edgeCases -> edgeCases.includeOnly(0L, months));

		Arbitrary<YearMonth> yearMonths = month.map(effectiveMin::plusMonths);

		if (allowedMonths.get().size() < 12) {
			yearMonths = yearMonths.filter(yearMonth -> allowedMonths.get().contains(yearMonth.getMonth()));
		}

		if (!withLeapYears.get()) {
			yearMonths = yearMonths.filter(date -> !new GregorianCalendar().isLeapYear(date.getYear()));
		}

		return yearMonths;
	}

	@Override
	public YearMonthArbitrary atTheEarliest(YearMonth min) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.yearMonthBetween.set(min, null);
		return clone;
	}

	@Override
	public YearMonthArbitrary atTheLatest(YearMonth max) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.yearMonthBetween.set(null, max);
		return clone;
	}

	@Override
	public YearMonthArbitrary yearBetween(Year min, Year max) {
		YearBetween yearBetween = (YearBetween) new YearBetween().set(min, max);
		DefaultYearMonthArbitrary clone = typedClone();
		clone.yearMonthBetween.setYearBetween(yearBetween);
		return clone;
	}

	@Override
	public YearMonthArbitrary monthBetween(Month min, Month max) {
		MonthBetween monthBetween = (MonthBetween) new MonthBetween().set(min, max);
		DefaultYearMonthArbitrary clone = typedClone();
		clone.allowedMonths.setBetween(monthBetween);
		return clone;
	}

	@Override
	public YearMonthArbitrary onlyMonths(Month... months) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.allowedMonths.set(months);
		return clone;
	}

	@Override
	public YearMonthArbitrary leapYears(boolean withLeapYears) {
		DefaultYearMonthArbitrary clone = typedClone();
		clone.withLeapYears.set(withLeapYears);
		return clone;
	}

}
