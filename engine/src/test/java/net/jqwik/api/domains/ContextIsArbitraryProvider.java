package net.jqwik.api.domains;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

class ContextIsArbitraryProvider extends DomainContextBase implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(String.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.singleton(Arbitraries.strings().numeric().ofLength(2));
	}
}
