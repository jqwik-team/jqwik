package net.jqwik.execution.providers;

import net.jqwik.api.*;
import net.jqwik.execution.*;

import java.util.*;
import java.util.function.*;

public class EnumArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isEnum();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		// noinspection unchecked
		return Arbitraries.of((Class<Enum>) targetType.getRawType());
	}
}
