package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.function.*;

public class WildcardArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		if (!targetType.isTypeVariableOrWildcard())
			return false;
		return !targetType.hasUpperBounds() && !targetType.hasLowerBounds();
	}

	@Override
	public Arbitrary<?> provideFor(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider) {
		return new WildcardArbitrary();
	}
}
