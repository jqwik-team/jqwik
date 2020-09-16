package net.jqwik.engine.providers;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

public class EnumArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isEnum();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.singleton(Arbitraries.of((Class<? extends Enum>) targetType.getRawType()));
	}

}
