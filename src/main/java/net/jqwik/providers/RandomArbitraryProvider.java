package net.jqwik.providers;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

public class RandomArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isOfType(Random.class);
	}

	@Override
	public Arbitrary<?> provideFor(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeSupplier) {
		return Arbitraries.randoms();
	}
}
