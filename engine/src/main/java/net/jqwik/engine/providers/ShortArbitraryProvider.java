package net.jqwik.engine.providers;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

public class ShortArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(Short.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.singleton(Arbitraries.shorts());
	}
}
