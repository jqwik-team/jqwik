package net.jqwik.time;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

public class DefaultDaysOfMonthArbitrary extends ArbitraryDecorator<Integer> implements DaysOfMonthArbitrary {

	private int startDayOfMonth = 1;
	private int endDayOfMonth = 31;

	@Override
	protected Arbitrary<Integer> arbitrary() {
		return Arbitraries.integers().between(startDayOfMonth, endDayOfMonth);
	}

	@Override
	public DaysOfMonthArbitrary greaterOrEqual(int min) {
		DefaultDaysOfMonthArbitrary clone = typedClone();
		min = Math.max(1, min);
		clone.startDayOfMonth = min;
		return clone;
	}

	@Override
	public DaysOfMonthArbitrary lessOrEqual(int max) {
		DefaultDaysOfMonthArbitrary clone = typedClone();
		max = Math.min(31, max);
		clone.endDayOfMonth = max;
		return clone;
	}

}
