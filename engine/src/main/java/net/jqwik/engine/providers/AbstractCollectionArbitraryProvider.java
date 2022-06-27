package net.jqwik.engine.providers;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import static net.jqwik.engine.support.JqwikCollectors.toLinkedHashSet;

abstract class AbstractCollectionArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(getProvidedType());
	}

	protected abstract Class<?> getProvidedType();

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		TypeUsage elementType = targetType.getTypeArgument(0);
		Set<Arbitrary<?>> elementArbitraries = subtypeProvider.apply(elementType);
		return elementArbitraries.stream()
								 .map(this::create)
								 .collect(toLinkedHashSet());
	}

	protected abstract Arbitrary<?> create(Arbitrary<?> innerArbitrary);

}
