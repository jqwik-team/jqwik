package net.jqwik.time.internal.properties.configurators;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class DateRangeForDateConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Date.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, DateRange range) {
		Date min = isoDateToDate(range.min(), false);
		Date max = isoDateToDate(range.max(), true);
		if (arbitrary instanceof DateArbitrary) {
			DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
			return dateArbitrary.between(min, max);
		} else {
			return arbitrary.filter(v -> filter((Date) v, min, max));
		}
	}

	private boolean filter(Date date, Date min, Date max) {
		return date.compareTo(min) >= 0 && date.compareTo(max) <= 0;
	}

	private Date isoDateToDate(String iso, boolean max) {
		Calendar calendar = DateRangeForCalendarConfigurator.isoDateToCalendar(iso, max);
		return calendar.getTime();
	}

}
