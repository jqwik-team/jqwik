package net.jqwik.execution.providers;

import java.util.function.*;

import net.jqwik.execution.*;
import net.jqwik.properties.*;

public interface ArbitraryProvider {

	boolean canProvideFor(GenericType targetType);

	/**
	 * @return true if the provider produces arbitraries for a generic type with type arguments
	 */
	boolean isGenericallyTyped();

	Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeProvider);
}
