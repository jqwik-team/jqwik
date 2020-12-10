package net.jqwik.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

public class DefaultDateArbitrary extends ArbitraryDecorator<LocalDate> implements DateArbitrary {

	private int yearMin = LocalDate.MIN.getYear();
	private int yearMax = LocalDate.MAX.getYear();

	@Override
	protected Arbitrary<LocalDate> arbitrary() {
		Arbitrary<Integer> year = generateYear();
		return Combinators.combine(year, year).as((y, y2) -> LocalDate.of(y, 12, 9));
	}

	private Arbitrary<Integer> generateYear(){
		return Arbitraries.integers().between(yearMin, yearMax);
	}

	@Override
	public DateArbitrary atTheEarliest(LocalDate date) {
		DefaultDateArbitrary clone = typedClone();
		return clone;
	}

	@Override
	public DateArbitrary atTheLatest(LocalDate date) {
		DefaultDateArbitrary clone = typedClone();
		return clone;
	}

	@Override
	public DateArbitrary yearGreaterOrEqual(int min) {
		DefaultDateArbitrary clone = typedClone();
		min = Math.max(min, LocalDate.MIN.getYear());
		clone.yearMin = min;
		return clone;
	}

	@Override
	public DateArbitrary yearLessOrEqual(int max) {
		DefaultDateArbitrary clone = typedClone();
		max = Math.min(max, LocalDate.MAX.getYear());
		clone.yearMax = max;
		return clone;
	}

	@Override
	public DateArbitrary monthGreaterOrEqual(Month min) {
		DefaultDateArbitrary clone = typedClone();
		return clone;
	}

	@Override
	public DateArbitrary monthLessOrEqual(Month max) {
		DefaultDateArbitrary clone = typedClone();
		return clone;
	}

	@Override
	public DateArbitrary dayOfMonthGreaterOrEqual(int min) {
		DefaultDateArbitrary clone = typedClone();
		return clone;
	}

	@Override
	public DateArbitrary dayOfMonthLessOrEqual(int max) {
		DefaultDateArbitrary clone = typedClone();
		return clone;
	}

	@Override
	public DateArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		DefaultDateArbitrary clone = typedClone();
		return clone;
	}

}
