package net.jqwik.execution.providers;

import net.jqwik.api.*;
import net.jqwik.execution.*;

import java.util.*;
import java.util.function.*;

abstract class AbstractCollectionArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(getProvidedType());
	}

	protected abstract Class<?> getProvidedType();

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		GenericType innerType = targetType.getTypeArguments()[0];
		return subtypeProvider.apply(innerType) //
			.map(this::create) //
			.orElse(null);
	}

	protected abstract Arbitrary<?> create(Arbitrary<?> innerArbitrary);
}
