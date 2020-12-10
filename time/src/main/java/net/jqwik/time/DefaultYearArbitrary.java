package net.jqwik.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

public class DefaultYearArbitrary extends ArbitraryDecorator<Year> implements YearArbitrary {

	private Year min = Year.of(Year.MIN_VALUE);
	private Year max = Year.of(Year.MAX_VALUE);

	@Override
	protected Arbitrary<Year> arbitrary() {
		Arbitrary<Integer> years = Arbitraries.integers().between(min.getValue(), max.getValue());
		return years.map(v -> Year.of(v));
	}

	@Override
	public YearArbitrary greaterOrEqual(Year min) {
		DefaultYearArbitrary clone = typedClone();
		min = Year.of(Math.max(min.getValue(), Year.MIN_VALUE));
		clone.min = min;
		return clone;
	}

	@Override
	public YearArbitrary lessOrEqual(Year max) {
		DefaultYearArbitrary clone = typedClone();
		max = Year.of(Math.min(max.getValue(), Year.MAX_VALUE));
		clone.max = max;
		return clone;
	}

}
