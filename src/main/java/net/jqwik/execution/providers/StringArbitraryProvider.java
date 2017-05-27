package net.jqwik.execution.providers;

import net.jqwik.execution.*;
import net.jqwik.newArbitraries.*;
import net.jqwik.properties.*;

public class StringArbitraryProvider implements SimpleArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isAssignableFrom(String.class);
	}

	@Override
	public NArbitrary<?> provideFor(GenericType targetType) {
		return NArbitraries.string();
	}
}
