package net.jqwik.time.internal.properties.configurators;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class YearRangeForDateConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Date.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, YearRange range) {
		int min = range.min();
		int max = range.max();
		if (arbitrary instanceof DateArbitrary) {
			DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
			return dateArbitrary.yearBetween(min, max);
		} else {
			return arbitrary.filter(v -> filter((Date) v, min, max));
		}
	}

	private boolean filter(Date date, int min, int max) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		if (calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
			year *= -1;
		}
		return year >= min && year <= max;
	}

}
