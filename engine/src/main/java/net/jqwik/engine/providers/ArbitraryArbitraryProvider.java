package net.jqwik.engine.providers;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;

public class ArbitraryArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isOfType(Arbitrary.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		TypeUsage innerType = targetType.getTypeArguments().get(0);
		return subtypeProvider.apply(innerType).stream()
							  .map(Arbitraries::just)
							  .collect(JqwikCollectors.toLinkedHashSet());
	}
}
