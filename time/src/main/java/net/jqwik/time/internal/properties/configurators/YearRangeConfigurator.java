package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class YearRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDate.class)
					   || targetType.isAssignableFrom(Calendar.class)
					   || targetType.isAssignableFrom(Date.class)
					   || targetType.isAssignableFrom(YearMonth.class)
					   || targetType.isAssignableFrom(Year.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, YearRange range) {
		if (arbitrary instanceof LocalDateArbitrary) {
			LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
			return localDateArbitrary.yearBetween(range.min(), range.max());
		} else if (arbitrary instanceof CalendarArbitrary) {
			CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
			return calendarArbitrary.yearBetween(range.min(), range.max());
		} else if (arbitrary instanceof DateArbitrary) {
			DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
			return dateArbitrary.yearBetween(range.min(), range.max());
		} else if (arbitrary instanceof YearMonthArbitrary) {
			YearMonthArbitrary yearMonthArbitrary = (YearMonthArbitrary) arbitrary;
			return yearMonthArbitrary.yearBetween(range.min(), range.max());
		} else if (arbitrary instanceof YearArbitrary) {
			YearArbitrary yearArbitrary = (YearArbitrary) arbitrary;
			return yearArbitrary.between(range.min(), range.max());
		} else {
			return arbitrary;
		}
	}
}
