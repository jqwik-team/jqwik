package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class YearRangeForYearConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Year.class);
	}

	public Arbitrary<?> configure(Arbitrary<Year> arbitrary, YearRange range) {
		int min = range.min();
		int max = range.max();
		if (arbitrary instanceof YearArbitrary) {
			YearArbitrary yearArbitrary = (YearArbitrary) arbitrary;
			return yearArbitrary.between(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private boolean filter(Year year, int min, int max) {
		return year.getValue() >= min && year.getValue() <= max;
	}

}
