package net.jqwik.execution.providers;

import net.jqwik.api.*;
import net.jqwik.execution.*;

import java.util.*;
import java.util.function.*;

public class StringArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(String.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		return Arbitraries.strings();
	}
}
