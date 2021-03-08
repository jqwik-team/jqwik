package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class DayOfMonthRangeForLocalDateConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDate.class);
	}

	public Arbitrary<?> configure(Arbitrary<LocalDate> arbitrary, DayOfMonthRange range) {
		int min = range.min();
		int max = range.max();
		if (arbitrary instanceof LocalDateArbitrary) {
			LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
			return localDateArbitrary.dayOfMonthBetween(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private boolean filter(LocalDate date, int min, int max) {
		return date.getDayOfMonth() >= min && date.getDayOfMonth() <= max;
	}

}
