package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.function.*;

public class WildcardArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		if (!targetType.isTypeVariableOrWildcard())
			return false;
		return !targetType.hasUpperBounds() && !targetType.hasLowerBounds();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		return new WildcardArbitrary();
	}
}
