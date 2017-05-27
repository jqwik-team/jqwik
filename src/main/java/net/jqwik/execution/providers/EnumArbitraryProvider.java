package net.jqwik.execution.providers;

import net.jqwik.execution.*;
import net.jqwik.properties.*;

public class EnumArbitraryProvider implements SimpleArbitraryProvider {

	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isEnum();
	}

	@Override
	public NArbitrary<?> provideFor(GenericType targetType) {
		// noinspection unchecked
		return NArbitraries.of((Class<Enum>) targetType.getRawType());
	}
}
