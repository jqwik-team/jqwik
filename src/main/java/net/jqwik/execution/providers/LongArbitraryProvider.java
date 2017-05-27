package net.jqwik.execution.providers;

import net.jqwik.execution.*;
import net.jqwik.properties.*;

public class LongArbitraryProvider implements SimpleArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(Long.class);
	}

	@Override
	public NArbitrary<?> provideFor(GenericType targetType) {
		return NArbitraries.longInteger();
	}
}
