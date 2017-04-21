package net.jqwik.execution.providers;

import net.jqwik.execution.*;
import net.jqwik.properties.*;

import java.util.function.*;

public class EnumArbitraryProvider implements SimpleArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isEnum();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType) {
		// noinspection unchecked
		return Arbitraries.of((Class<Enum>) targetType.getRawType());
	}
}
