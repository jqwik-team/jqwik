package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

abstract class AbstractCollectionArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return TypeUsage.of(getProvidedType()).canBeAssignedTo(targetType);
	}

	protected abstract Class<?> getProvidedType();

	@Override
	public Set<Arbitrary<?>> provideArbitrariesFor(
		TypeUsage targetType, Function<TypeUsage, Set<Arbitrary<?>>> subtypeProvider
	) {
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
