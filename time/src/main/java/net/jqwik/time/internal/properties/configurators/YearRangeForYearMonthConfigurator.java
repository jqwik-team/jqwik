package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class YearRangeForYearMonthConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(YearMonth.class);
	}

	public Arbitrary<?> configure(Arbitrary<YearMonth> arbitrary, YearRange range) {
		int min = range.min();
		int max = range.max();
		if (arbitrary instanceof YearMonthArbitrary) {
			YearMonthArbitrary yearMonthArbitrary = (YearMonthArbitrary) arbitrary;
			return yearMonthArbitrary.yearBetween(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private boolean filter(YearMonth yearMonth, int min, int max) {
		return yearMonth.getYear() >= min && yearMonth.getYear() <= max;
	}

}
