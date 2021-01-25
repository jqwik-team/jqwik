package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.time.format.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class MonthDayRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(MonthDay.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, MonthDayRange range) {
		if (arbitrary instanceof MonthDayArbitrary) {
			MonthDayArbitrary monthDayArbitrary = (MonthDayArbitrary) arbitrary;
			return monthDayArbitrary.between(isoDateToMonthDay(range.min()), isoDateToMonthDay(range.max()));
		} else {
			return arbitrary;
		}
	}

	private MonthDay isoDateToMonthDay(String iso) {
		if (iso == null) {
			throw new NullPointerException("Argument is null");
		} else if (!iso.startsWith("--")) {
			throw new DateTimeParseException("MonthDay must start with --. (Example: --05-25)", iso, 0);
		}
		iso = iso.substring(2);
		String[] parts = iso.split("-");
		if (parts.length != 2) {
			throw new DateTimeParseException("MonthDay must consist of two parts. (Example: --05-25)", iso, 2);
		}
		int month, day;
		try {
			month = Integer.parseInt(parts[0]);
			day = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			throw new DateTimeParseException("MonthDay parts may only consist of digits. (Example: --05-25)", iso, 2);
		}
		return MonthDay.of(month, day);
	}

}
