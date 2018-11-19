package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;

public class WildcardArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		if (!targetType.isWildcard())
			return false;
		return !targetType.hasUpperBounds() && !targetType.hasLowerBounds();
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.singleton(new WildcardArbitrary());
	}

	@Override
	public int priority() {
		return 100;
	}
}
