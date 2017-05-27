package net.jqwik.execution.providers;

import java.util.function.*;

import net.jqwik.execution.*;
import net.jqwik.properties.*;

public interface SimpleArbitraryProvider extends ArbitraryProvider {

	default boolean needsSubtypeProvider() {
		return false;
	}

	@Override
	default NArbitrary<?> provideFor(GenericType targetType, Function<GenericType, NArbitrary<?>> subtypeSupplier) {
		return provideFor(targetType);
	}

	NArbitrary<?> provideFor(GenericType targetType);

}
