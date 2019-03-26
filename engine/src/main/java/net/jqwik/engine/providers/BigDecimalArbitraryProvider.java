package net.jqwik.engine.providers;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

public class BigDecimalArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(BigDecimal.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.singleton(Arbitraries.bigDecimals());
	}
}
