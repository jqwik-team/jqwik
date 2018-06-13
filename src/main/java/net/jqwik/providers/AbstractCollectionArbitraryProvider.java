package net.jqwik.providers;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

abstract class AbstractCollectionArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isOfType(getProvidedType());
	}

	protected abstract Class<?> getProvidedType();

	@Override
	public Arbitrary<?> provideFor(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider) {
		TypeUsage innerType = targetType.getTypeArguments().get(0);
		return subtypeProvider.apply(innerType) //
				.map(this::create) //
				.orElse(null);
	}

	protected abstract Arbitrary<?> create(Arbitrary<?> innerArbitrary);

}
