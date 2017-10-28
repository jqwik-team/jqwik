package net.jqwik.execution.providers;

import java.util.function.Function;

import net.jqwik.api.Arbitrary;
import net.jqwik.execution.GenericType;

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
