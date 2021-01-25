package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.time.format.*;

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
		if (iso == null) {
			throw new NullPointerException("Argument is null");
		} else if (iso.length() == 0) {
			throw new DateTimeParseException("YearMonth length can not be 0. (Example: 2013-05)", iso, 0);
		}
		String[] parts = iso.split("-");
		if (parts.length != 2) {
			throw new DateTimeParseException("YearMonth must consist of two parts. (Example: 2013-05)", iso, 0);
		}
		int year, month;
		try {
			year = Integer.parseInt(parts[0]);
			month = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			throw new DateTimeParseException("YearMonth parts may only consist of digits. (Example: 2013-05)", iso, 0);
		}
		return YearMonth.of(year, month);
	}

}
