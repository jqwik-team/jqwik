package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class LeapYearsForLocalDateConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDate.class);
	}

	public Arbitrary<?> configure(Arbitrary<LocalDate> arbitrary, LeapYears leapYears) {
		boolean withLeapYears = leapYears.withLeapYears();
		if (arbitrary instanceof LocalDateArbitrary) {
			LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
			return localDateArbitrary.leapYears(withLeapYears);
		} else {
			return arbitrary.filter(v -> filter(v, withLeapYears));
		}
	}

	private boolean filter(LocalDate date, boolean withLeapYears) {
		return withLeapYears || !(new GregorianCalendar().isLeapYear(date.getYear()));
	}

}
