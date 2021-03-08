package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

public class DayOfWeekRangeForDateConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Date.class);
	}

	public Arbitrary<?> configure(Arbitrary<Date> arbitrary, DayOfWeekRange range) {
		DayOfWeek[] dayOfWeeks = createDayOfWeekArray(range);
		if (arbitrary instanceof DateArbitrary) {
			DateArbitrary dateArbitrary = (DateArbitrary) arbitrary;
			return dateArbitrary.onlyDaysOfWeek(dayOfWeeks);
		} else {
			return arbitrary.filter(v -> filter(v, dayOfWeeks));
		}
	}

	private DayOfWeek[] createDayOfWeekArray(DayOfWeekRange range) {
		List<DayOfWeek> dayOfWeeks = new ArrayList<>();
		for (int i = range.min().getValue(); i <= range.max().getValue(); i++) {
			dayOfWeeks.add(DayOfWeek.of(i));
		}
		return dayOfWeeks.toArray(new DayOfWeek[]{});
	}

	private boolean filter(Date date, DayOfWeek[] dayOfWeeks) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		DayOfWeek value = DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
		for (DayOfWeek dayOfWeek : dayOfWeeks) {
			if (value == dayOfWeek) {
				return true;
			}
		}
		return false;
	}

}
