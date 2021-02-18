package net.jqwik.time.internal.properties.providers;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.*;

public class ZoneOffsetsArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(ZoneOffset.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		if (targetType.isAssignableFrom(ZoneOffset.class)) {
			return Collections.singleton(Times.zoneOffsets());
		} else {
			return null;
		}
	}
}
