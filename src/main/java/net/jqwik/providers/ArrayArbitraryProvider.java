package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.util.*;
import java.util.function.*;

public class ArrayArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isArray();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		return subtypeProvider.apply(targetType.getComponentType()) //
			.map(elementArbitrary -> Arbitraries.arrayOf(targetType.getRawType(), elementArbitrary)) //
			.orElse(null);
	}
}
