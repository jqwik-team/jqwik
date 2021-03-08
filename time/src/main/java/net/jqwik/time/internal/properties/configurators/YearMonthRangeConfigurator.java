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

	public Arbitrary<?> configure(Arbitrary<YearMonth> arbitrary, YearMonthRange range) {
		YearMonth min = isoDateToYearMonth(range.min());
		YearMonth max = isoDateToYearMonth(range.max());
		if (arbitrary instanceof YearMonthArbitrary) {
			YearMonthArbitrary yearMonthArbitrary = (YearMonthArbitrary) arbitrary;
			return yearMonthArbitrary.between(min, max);
		} else {
			return arbitrary.filter(v -> filter(v, min, max));
		}
	}

	private YearMonth isoDateToYearMonth(String iso) {
		return YearMonth.parse(iso);
	}

	private boolean filter(YearMonth yearMonth, YearMonth min, YearMonth max) {
		return !yearMonth.isBefore(min) && !yearMonth.isAfter(max);
	}

}
