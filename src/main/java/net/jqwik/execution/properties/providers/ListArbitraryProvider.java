package net.jqwik.execution.properties.providers;

import javaslang.test.Arbitrary;
import net.jqwik.api.properties.Generator;
import net.jqwik.execution.properties.GenericType;

import java.util.List;
import java.util.function.Function;

public class ListArbitraryProvider implements TypedArbitraryProvider {

	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(List.class) && targetType.isGeneric();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeProvider) {
		GenericType innerType = targetType.getTypeArguments()[0];
		Arbitrary<?> innerArbitrary = subtypeProvider.apply(innerType);
		if (innerArbitrary != null)
			return Generator.list(innerArbitrary);
		return null;
	}
}
