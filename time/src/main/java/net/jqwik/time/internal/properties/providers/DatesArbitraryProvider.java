package net.jqwik.time.internal.properties.providers;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.*;

public class DatesArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDate.class) || targetType.isAssignableFrom(Calendar.class) || targetType
																													  .isAssignableFrom(Date.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		if (targetType.isAssignableFrom(LocalDate.class)) {
			return Collections.singleton(Dates.dates());
		} else if (targetType.isAssignableFrom(Calendar.class)) {
			return Collections.singleton(Dates.datesAsCalendar());
		} else if (targetType.isAssignableFrom(Date.class)) {
			return Collections.singleton(Dates.datesAsDate());
		} else {
			return null;
		}
	}
}
