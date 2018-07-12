package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.util.*;

public class IntegerArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(Integer.class);
	}

	@Override
	public Set<Arbitrary<?>> provideArbitrariesFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.singleton(Arbitraries.integers());
	}
}
