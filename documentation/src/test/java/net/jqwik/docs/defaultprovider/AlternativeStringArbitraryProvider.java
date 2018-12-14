package net.jqwik.docs.defaultprovider;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.util.*;

public class AlternativeStringArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(String.class);
	}

	@Override
	public int priority() {
		return -1;
		// return 1;
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.singleton(Arbitraries.constant("A String"));
	}
}
