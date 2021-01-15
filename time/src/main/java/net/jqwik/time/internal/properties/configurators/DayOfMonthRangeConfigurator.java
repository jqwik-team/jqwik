package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class DayOfMonthRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDate.class) || targetType.isAssignableFrom(Integer.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, DayOfMonthRange range) {
		if (arbitrary instanceof DateArbitrary) {
			DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
			return dateArbitrary.dayOfMonthBetween(range.min(), range.max());
		} else if (arbitrary instanceof IntegerArbitrary) {
			IntegerArbitrary dayOfMonthsArbitrary = (IntegerArbitrary) arbitrary;
			return dayOfMonthsArbitrary.between(range.min(), range.max());
		} else {
			return arbitrary;
		}
	}
}
