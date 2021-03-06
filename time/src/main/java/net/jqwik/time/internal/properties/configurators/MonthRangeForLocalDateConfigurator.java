package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class MonthRangeForLocalDateConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDate.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, MonthRange range) {
		Month min = range.min();
		Month max = range.max();
		if (arbitrary instanceof LocalDateArbitrary) {
			LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
			return localDateArbitrary.monthBetween(min, max);
		} else {
			return arbitrary.filter(v -> filter((LocalDate) v, min, max));
		}
	}

	private boolean filter(LocalDate date, Month min, Month max) {
		return date.getMonth().compareTo(min) >= 0 && date.getMonth().compareTo(max) <= 0;
	}

}
