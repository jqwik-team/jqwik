package net.jqwik.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

public class DefaultYearMonthArbitrary extends ArbitraryDecorator<YearMonth> implements YearMonthArbitrary {

	@Override
	protected Arbitrary<YearMonth> arbitrary() {
		return Arbitraries.just(YearMonth.of(2020, 12));
	}

	@Override
	public YearMonthArbitrary atTheEarliest(YearMonth yearMonth) {
		return null;
	}

	@Override
	public YearMonthArbitrary atTheLatest(YearMonth yearMonth) {
		return null;
	}

	@Override
	public YearMonthArbitrary yearGreaterOrEqual(Year min) {
		return null;
	}

	@Override
	public YearMonthArbitrary yearLessOrEqual(Year min) {
		return null;
	}

	@Override
	public YearMonthArbitrary monthGreaterOrEqual(Month min) {
		return null;
	}

	@Override
	public YearMonthArbitrary monthLessOrEqual(Month max) {
		return null;
	}

	@Override
	public YearMonthArbitrary onlyMonths(Month... months) {
		return null;
	}
}
