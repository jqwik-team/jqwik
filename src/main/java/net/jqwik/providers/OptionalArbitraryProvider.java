package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.util.*;
import java.util.stream.*;

public class OptionalArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isOfType(Optional.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		TypeUsage innerType = targetType.getTypeArguments().get(0);
		return subtypeProvider.apply(innerType).stream() //
							  .map(Arbitrary::optional)
							  .collect(Collectors.toSet());
	}
}
