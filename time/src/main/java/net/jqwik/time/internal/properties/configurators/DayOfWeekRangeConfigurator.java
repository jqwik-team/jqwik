package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class DayOfWeekRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDate.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, DayOfWeekRange range) {
		if (arbitrary instanceof LocalDateArbitrary) {
			LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
			List<DayOfWeek> dayOfWeeks = new ArrayList<DayOfWeek>();
			for (int i = range.min().getValue(); i <= range.max().getValue(); i++) {
				dayOfWeeks.add(DayOfWeek.of(i));
			}
			return localDateArbitrary.onlyDaysOfWeek(dayOfWeeks.toArray(new DayOfWeek[]{}));
		} else {
			return arbitrary;
		}
	}
}
