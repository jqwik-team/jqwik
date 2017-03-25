package net.jqwik.execution.properties;

import javaslang.test.Arbitrary;
import net.jqwik.api.properties.Generator;

import java.util.List;
import java.util.function.Function;

public class ListArbitraryProvider implements TypedArbitraryProvider {

	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(List.class) && targetType.isGeneric();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType providerType, Function<GenericType, Arbitrary<?>> subtypeSupplier) {
		GenericType innerType = providerType.getTypeArguments()[0];
		Arbitrary<?> innerArbitrary = subtypeSupplier.apply(innerType);
		if (innerArbitrary != null)
			return Generator.list(innerArbitrary);
		return null;
	}
}
