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
		if (iso == null || iso.length() == 0) {
			return null;
		}
		String[] parts = iso.split("-");
		if (parts.length != 2) {
			return null;
		}
		int year, month;
		try {
			year = Integer.parseInt(parts[0]);
			month = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			return null;
		}
		return YearMonth.of(year, month);
	}

}
