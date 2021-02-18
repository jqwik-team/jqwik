package net.jqwik.time.internal.properties.providers;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.*;

public class ZoneIdArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(ZoneId.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		if (targetType.isAssignableFrom(ZoneId.class)) {
			return Collections.singleton(Times.zoneIds());
		} else {
			return null;
		}
	}
}
