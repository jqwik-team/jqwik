package net.jqwik.execution.properties.providers;

import java.util.function.*;

import javaslang.test.*;
import net.jqwik.execution.properties.*;

abstract class AbstractCollectionArbitraryProvider implements TypedArbitraryProvider {

	@Override
	public boolean canProvideFor(GenericType targetType, boolean withName) {
		return targetType.isAssignableFrom(getProvidedType()) && targetType.isGeneric();
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
