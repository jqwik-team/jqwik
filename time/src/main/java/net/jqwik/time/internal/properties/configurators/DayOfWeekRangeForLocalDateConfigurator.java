package net.jqwik.time.internal.properties.configurators;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class DayOfWeekRangeForLocalDateConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDate.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, DayOfWeekRange range) {
		DayOfWeek[] dayOfWeeks = createDayOfWeekArray(range);
		if (arbitrary instanceof LocalDateArbitrary) {
			LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
			return localDateArbitrary.onlyDaysOfWeek(dayOfWeeks);
		} else {
			return arbitrary.filter(v -> filter((LocalDate) v, dayOfWeeks));
		}
	}

	private DayOfWeek[] createDayOfWeekArray(DayOfWeekRange range) {
		List<DayOfWeek> dayOfWeeks = new ArrayList<>();
		for (int i = range.min().getValue(); i <= range.max().getValue(); i++) {
			dayOfWeeks.add(DayOfWeek.of(i));
		}
		return dayOfWeeks.toArray(new DayOfWeek[]{});
	}

	private boolean filter(LocalDate date, DayOfWeek[] dayOfWeeks) {
		for (DayOfWeek dayOfWeek : dayOfWeeks) {
			if (date.getDayOfWeek() == dayOfWeek) {
				return true;
			}
		}
		return false;
	}

}
