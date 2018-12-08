package net.jqwik.engine.providers;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

abstract class AbstractCollectionArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(getProvidedType());
	}

	protected abstract Class<?> getProvidedType();

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		TypeUsage innerType = targetType.getTypeArguments().isEmpty() ? //
			TypeUsage.forType(Object.class) //
			: targetType.getTypeArguments().get(0);
		Set<Arbitrary<?>> elementArbitraries = subtypeProvider.apply(innerType);
		return elementArbitraries.stream() //
								 .map(this::create) //
								 .collect(Collectors.toSet());
	}

	protected abstract Arbitrary<?> create(Arbitrary<?> innerArbitrary);

}
