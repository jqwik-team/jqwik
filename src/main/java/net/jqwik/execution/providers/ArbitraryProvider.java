package net.jqwik.execution.providers;

import java.util.function.Function;

import net.jqwik.api.Arbitrary;
import net.jqwik.execution.GenericType;

public interface ArbitraryProvider {

	boolean canProvideFor(GenericType targetType);

	/**
	 * @return true if the provider produces arbitraries for a generic type with type arguments
	 */
	boolean needsSubtypeProvider();

	Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeProvider);
}
