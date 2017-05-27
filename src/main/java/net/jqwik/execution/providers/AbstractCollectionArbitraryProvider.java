package net.jqwik.execution.providers;

import java.util.function.*;

import net.jqwik.execution.*;
import net.jqwik.properties.*;

abstract class AbstractCollectionArbitraryProvider implements GenericArbitraryProvider {

	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(getProvidedType());
	}

	protected abstract Class<?> getProvidedType();

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeProvider) {
		GenericType innerType = targetType.getTypeArguments()[0];
		Arbitrary<?> innerArbitrary = subtypeProvider.apply(innerType);
		if (innerArbitrary != null)
			return create(innerArbitrary);
		return null;
	}

	protected abstract Arbitrary<?> create(Arbitrary<?> innerArbitrary);
}
