package net.jqwik.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

public class DefaultDateArbitrary extends ArbitraryDecorator<LocalDate> implements DateArbitrary {

	@Override
	protected Arbitrary<LocalDate> arbitrary() {
		return Arbitraries.just(LocalDate.of(2020, 12, 9));
	}

	@Override
	public DateArbitrary atTheEarliest(LocalDate date) {
		return typedClone();
	}

	@Override
	public DateArbitrary atTheLatest(LocalDate date) {
		return typedClone();
	}

	@Override
	public DateArbitrary yearGreaterOrEqual(int min) {
		return typedClone();
	}

	@Override
	public DateArbitrary yearLessOrEqual(int max) {
		return typedClone();
	}

	@Override
	public DateArbitrary monthGreaterOrEqual(Month min) {
		return typedClone();
	}

	@Override
	public DateArbitrary monthLessOrEqual(Month max) {
		return typedClone();
	}

	@Override
	public DateArbitrary dayOfMonthGreaterOrEqual(int min) {
		return typedClone();
	}

	@Override
	public DateArbitrary dayOfMonthLessOrEqual(int max) {
		return typedClone();
	}

	@Override
	public DateArbitrary onlyDaysOfWeek(DayOfWeek... daysOfWeek) {
		return typedClone();
	}

}
