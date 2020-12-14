package net.jqwik.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

public class DefaultDaysOfWeekArbitrary extends ArbitraryDecorator<DayOfWeek> implements DaysOfWeekArbitrary {

	private DayOfWeek[] allowedDayOfWeeks = new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};

	@Override
	protected Arbitrary<DayOfWeek> arbitrary() {
		Arbitrary<DayOfWeek> daysOfWeek = Arbitraries.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		daysOfWeek = daysOfWeek.filter(v -> isInAllowedDayOfWeeks(v));
		return daysOfWeek;
	}

	private boolean isInAllowedDayOfWeeks(DayOfWeek dayOfWeek) {
		if (allowedDayOfWeeks == null) {
			return false;
		}
		for (DayOfWeek d : allowedDayOfWeeks) {
			if (d.equals(dayOfWeek)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public DaysOfWeekArbitrary only(DayOfWeek... dayOfWeeks) {
		DefaultDaysOfWeekArbitrary clone = typedClone();
		clone.allowedDayOfWeeks = dayOfWeeks;
		return clone;
	}
}
