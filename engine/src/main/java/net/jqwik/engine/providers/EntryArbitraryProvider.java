package net.jqwik.engine.providers;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.support.*;

public class EntryArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(Map.Entry.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		TypeUsage keyType = targetType.getTypeArgument(0);
		TypeUsage valueType = targetType.getTypeArgument(1);

		return subtypeProvider
			.resolveAndCombine(keyType, valueType)
			.map(arbitraries -> {
				Arbitrary<?> keyArbitrary = arbitraries.get(0);
				Arbitrary<?> valueArbitrary = arbitraries.get(1);
				return Arbitraries.entries(keyArbitrary, valueArbitrary);
			})
			.collect(CollectorsSupport.toLinkedHashSet());

	}
}
