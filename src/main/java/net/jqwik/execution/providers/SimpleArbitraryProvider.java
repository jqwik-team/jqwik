package net.jqwik.execution.providers;

import java.util.function.*;

import net.jqwik.execution.*;
import net.jqwik.properties.*;

public interface SimpleArbitraryProvider extends ArbitraryProvider {

	default boolean needsSubtypeProvider() {
		return false;
	}

	@Override
	default Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeSupplier) {
		return provideFor(targetType);
	}

	Arbitrary<?> provideFor(GenericType targetType);

}
