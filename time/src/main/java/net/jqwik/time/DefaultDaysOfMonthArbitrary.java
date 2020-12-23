package net.jqwik.time;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultDaysOfMonthArbitrary extends ArbitraryDecorator<Integer> implements DaysOfMonthArbitrary {

	private int startDayOfMonth = 1;
	private int endDayOfMonth = 31;

	@Override
	protected Arbitrary<Integer> arbitrary() {
		return Arbitraries.integers().between(startDayOfMonth, endDayOfMonth);
	}

	@Override
	public DaysOfMonthArbitrary between(int min, int max) {
		DefaultDaysOfMonthArbitrary clone = typedClone();
		min = Math.max(1, min);
		max = Math.min(31, max);
		clone.startDayOfMonth = min;
		clone.endDayOfMonth = max;
		return clone;
	}

}
