package net.jqwik.execution.providers;

import java.util.function.*;

import net.jqwik.execution.*;
import net.jqwik.newArbitraries.*;

abstract class AbstractCollectionArbitraryProvider implements GenericArbitraryProvider {

	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(getProvidedType());
	}

	protected abstract Class<?> getProvidedType();

	@Override
	public NArbitrary<?> provideFor(GenericType targetType, Function<GenericType, NArbitrary<?>> subtypeProvider) {
		GenericType innerType = targetType.getTypeArguments()[0];
		NArbitrary<?> innerArbitrary = subtypeProvider.apply(innerType);
		if (innerArbitrary != null)
			return create(innerArbitrary);
		return null;
	}

	protected abstract NArbitrary<?> create(NArbitrary<?> innerArbitrary);
}
