package net.jqwik.engine.providers;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

import java.util.*;

public class EmailArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(String.class) && targetType.findAnnotation(Email.class).isPresent();
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, ArbitraryProvider.SubtypeProvider subtypeProvider) {
		return Collections.singleton(Arbitraries.emails());
	}

	@Override
	public int priority() {
		return 5;
	}
}
