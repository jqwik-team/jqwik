package net.jqwik.time.providers;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.constraints.*;

public class DayOfMonthArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(Integer.class) && targetType.findAnnotation(DayOfMonth.class).isPresent();
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.singleton(Dates.daysOfMonth());
	}

	@Override
	public int priority() {
		return 5;
	}

}
