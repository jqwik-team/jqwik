package net.jqwik.execution.providers;

import java.util.function.*;

import net.jqwik.execution.*;
import net.jqwik.newArbitraries.*;

public interface ArbitraryProvider {

	boolean canProvideFor(GenericType targetType);

	/**
	 * @return true if the provider produces arbitraries for a generic type with type arguments
	 */
	boolean needsSubtypeProvider();

	NArbitrary<?> provideFor(GenericType targetType, Function<GenericType, NArbitrary<?>> subtypeProvider);
}
