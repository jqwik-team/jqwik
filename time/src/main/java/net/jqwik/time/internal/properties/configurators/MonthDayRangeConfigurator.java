package net.jqwik.time.internal.properties.configurators;

import java.time.*;

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
		if (iso == null || iso.length() == 0) {
			return null;
		}
		String[] parts = iso.split("-");
		if (parts.length != 2) {
			return null;
		}
		int month, day;
		try {
			month = Integer.parseInt(parts[0]);
			day = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			return null;
		}
		return MonthDay.of(month, day);
	}

}
