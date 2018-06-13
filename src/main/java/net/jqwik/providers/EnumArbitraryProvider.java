package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.util.*;
import java.util.function.*;

public class EnumArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isEnum();
	}

	@Override
	public Arbitrary<?> provideFor(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider) {
		// noinspection unchecked
		return Arbitraries.of((Class<Enum>) targetType.getRawType());
	}
}
