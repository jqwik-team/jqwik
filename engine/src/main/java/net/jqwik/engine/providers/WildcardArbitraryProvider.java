package net.jqwik.engine.providers;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.arbitraries.*;

public class WildcardArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		if (!targetType.isWildcard())
			return false;
		return hasNoUpperBoundOtherThanObject(targetType) && targetType.getLowerBounds().isEmpty();
	}

	private boolean hasNoUpperBoundOtherThanObject(TypeUsage targetType) {
		return targetType.getUpperBounds().size() != 1 ||
				   targetType.getUpperBounds().get(0).isOfType(Object.class);
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
