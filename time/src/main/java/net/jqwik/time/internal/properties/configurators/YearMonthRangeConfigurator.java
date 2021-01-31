package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class YearMonthRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(YearMonth.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, YearMonthRange range) {
		if (arbitrary instanceof YearMonthArbitrary) {
			YearMonthArbitrary yearMonthArbitrary = (YearMonthArbitrary) arbitrary;
			return yearMonthArbitrary.between(isoDateToYearMonth(range.min()), isoDateToYearMonth(range.max()));
		} else {
			return arbitrary;
		}
	}

	private YearMonth isoDateToYearMonth(String iso) {
		return YearMonth.parse(iso);
	}

}
