package net.jqwik.time.internal.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.constraints.*;

public class DayOfMonthRangeForIntegerConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Integer.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, DayOfMonthRange range) {
		int min = range.min();
		int max = range.max();
		if (arbitrary instanceof IntegerArbitrary) {
			IntegerArbitrary dayOfMonthsArbitrary = (IntegerArbitrary) arbitrary;
			return dayOfMonthsArbitrary.between(min, max);
		} else {
			return arbitrary.filter(v -> filter((int) v, min, max));
		}
	}

	private boolean filter(int dayOfMonth, int min, int max) {
		return dayOfMonth >= min && dayOfMonth <= max;
	}

}
