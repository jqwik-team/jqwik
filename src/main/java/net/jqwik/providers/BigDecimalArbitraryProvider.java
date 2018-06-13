package net.jqwik.providers;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

public class BigDecimalArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isOfType(BigDecimal.class);
	}

	@Override
	public Arbitrary<?> provideFor(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider) {
		return Arbitraries.bigDecimals();
	}
}
