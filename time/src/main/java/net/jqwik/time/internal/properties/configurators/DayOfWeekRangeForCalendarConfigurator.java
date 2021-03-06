package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

public class DayOfWeekRangeForCalendarConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Calendar.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, DayOfWeekRange range) {
		DayOfWeek[] dayOfWeeks = createDayOfWeekArray(range);
		if (arbitrary instanceof CalendarArbitrary) {
			CalendarArbitrary calendarArbitrary = (CalendarArbitrary) arbitrary;
			return calendarArbitrary.onlyDaysOfWeek(dayOfWeeks);
		} else {
			return arbitrary.filter(v -> filter((Calendar) v, dayOfWeeks));
		}
	}

	private DayOfWeek[] createDayOfWeekArray(DayOfWeekRange range) {
		List<DayOfWeek> dayOfWeeks = new ArrayList<>();
		for (int i = range.min().getValue(); i <= range.max().getValue(); i++) {
			dayOfWeeks.add(DayOfWeek.of(i));
		}
		return dayOfWeeks.toArray(new DayOfWeek[]{});
	}

	private boolean filter(Calendar date, DayOfWeek[] dayOfWeeks) {
		DayOfWeek value = DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(date.get(Calendar.DAY_OF_WEEK));
		for (DayOfWeek dayOfWeek : dayOfWeeks) {
			if (value == dayOfWeek) {
				return true;
			}
		}
		return false;
	}

}
