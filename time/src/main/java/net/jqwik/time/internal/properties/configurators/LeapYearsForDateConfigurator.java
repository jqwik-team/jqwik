package net.jqwik.time.internal.properties.configurators;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class LeapYearsForDateConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Date.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, LeapYears leapYears) {
		boolean withLeapYears = leapYears.withLeapYears();
		if (arbitrary instanceof DateArbitrary) {
			DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
			return dateArbitrary.leapYears(withLeapYears);
		} else {
			return arbitrary.filter(v -> filter((Date) v, withLeapYears));
		}
	}

	private boolean filter(Date date, boolean withLeapYears) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		if (calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
			year = -year;
		}
		return withLeapYears || !(new GregorianCalendar().isLeapYear(year));
	}

}
