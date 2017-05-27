package net.jqwik.execution.providers;

import java.util.function.*;

import net.jqwik.execution.*;
import net.jqwik.newArbitraries.*;

public class ArrayArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isArray();
	}

	@Override
	public boolean needsSubtypeProvider() {
		return true;
	}

	@Override
	public NArbitrary<?> provideFor(GenericType targetType, Function<GenericType, NArbitrary<?>> subtypeProvider) {
		return NArbitraries.arrayOf(targetType.getRawType(), subtypeProvider.apply(targetType.getComponentType()));
	}
}
