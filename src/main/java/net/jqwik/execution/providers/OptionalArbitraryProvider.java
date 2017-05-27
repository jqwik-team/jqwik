package net.jqwik.execution.providers;

import java.util.*;
import java.util.function.*;

import net.jqwik.execution.*;
import net.jqwik.properties.*;

public class OptionalArbitraryProvider implements GenericArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(Optional.class);
	}

	@Override
	public NArbitrary<?> provideFor(GenericType targetType, Function<GenericType, NArbitrary<?>> subtypeSupplier) {
		GenericType innerType = targetType.getTypeArguments()[0];
		NArbitrary<?> innerArbitrary = subtypeSupplier.apply(innerType);
		if (innerArbitrary != null)
			return NArbitraries.optionalOf(innerArbitrary);
		return null;
	}
}
