package net.jqwik.engine.providers;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

import static net.jqwik.engine.support.JqwikCollectors.toLinkedHashSet;

public class NullableArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isNullable() && !targetType.isAnnotated(WithNull.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(
		TypeUsage targetType,
		SubtypeProvider subtypeProvider
	) {
		TypeUsage nonNullType = targetType.asNotNullable();
		Set<Arbitrary<?>> rawArbitraries = subtypeProvider.apply(nonNullType);
		return rawArbitraries.stream()
				   .map(a -> a.injectNull(0.05))
				   .collect(toLinkedHashSet());
	}

	@Override
	public int priority() {
		// Replace most providers if type is nullable
		return 101;
	}
}
