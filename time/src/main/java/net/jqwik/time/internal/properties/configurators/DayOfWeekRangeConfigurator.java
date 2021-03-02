package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class DayOfWeekRangeConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDate.class)
					   || targetType.isAssignableFrom(Calendar.class)
					   || targetType.isAssignableFrom(Date.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, DayOfWeekRange range) {
		if (arbitrary instanceof LocalDateArbitrary) {
			LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
			return localDateArbitrary.onlyDaysOfWeek(createDayOfWeekArray(range));
		} else if (arbitrary instanceof CalendarArbitrary) {
			CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
			return calendarArbitrary.onlyDaysOfWeek(createDayOfWeekArray(range));
		} else {
			DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
			return dateArbitrary.onlyDaysOfWeek(createDayOfWeekArray(range));
		}
	}

	private DayOfWeek[] createDayOfWeekArray(DayOfWeekRange range) {
		List<DayOfWeek> dayOfWeeks = new ArrayList<>();
		for (int i = range.min().getValue(); i <= range.max().getValue(); i++) {
			dayOfWeeks.add(DayOfWeek.of(i));
		}
		return dayOfWeeks.toArray(new DayOfWeek[]{});
	}

}
