package net.jqwik.execution.properties.providers;

import java.util.*;
import java.util.function.*;

import javaslang.test.*;
import net.jqwik.api.properties.*;
import net.jqwik.execution.properties.*;

public class OptionalArbitraryProvider implements TypedArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType, boolean withName) {
		return targetType.isAssignableFrom(Optional.class) && targetType.isGeneric();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeSupplier) {
		// TODO: Should also generate null values within Optional
		GenericType innerType = targetType.getTypeArguments()[0];
		Arbitrary<?> innerArbitrary = subtypeSupplier.apply(innerType);
		if (innerArbitrary != null)
			return Generator.optionalOf(innerArbitrary);
		return null;
	}
}
