package net.jqwik.time.internal.properties.providers;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.*;

public class DateTimesArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDateTime.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.singleton(DateTimes.dateTimes());
	}

}
