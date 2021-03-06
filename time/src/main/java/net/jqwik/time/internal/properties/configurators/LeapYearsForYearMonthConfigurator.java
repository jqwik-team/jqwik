package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class LeapYearsForYearMonthConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(YearMonth.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, LeapYears leapYears) {
		boolean withLeapYears = leapYears.withLeapYears();
		if (arbitrary instanceof YearMonthArbitrary) {
			YearMonthArbitrary yearMonthArbitrary = (YearMonthArbitrary) arbitrary;
			return yearMonthArbitrary.leapYears(leapYears.withLeapYears());
		} else {
			return arbitrary.filter(v -> filter((YearMonth) v, withLeapYears));
		}
	}

	private boolean filter(YearMonth yearMonth, boolean withLeapYears) {
		return withLeapYears || !(new GregorianCalendar().isLeapYear(yearMonth.getYear()));
	}

}
