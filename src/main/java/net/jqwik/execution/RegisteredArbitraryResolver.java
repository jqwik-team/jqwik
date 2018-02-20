package net.jqwik.execution;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

class RegisteredArbitraryResolver {

	private final List<ArbitraryProvider> registeredProviders;

	RegisteredArbitraryResolver(List<ArbitraryProvider> registeredProviders) {
		this.registeredProviders = registeredProviders;
	}

	Optional<Arbitrary<?>> resolve(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		for (ArbitraryProvider provider : registeredProviders) {
			if (provider.canProvideFor(targetType)) {
				Arbitrary<?> arbitrary = provider.provideFor(targetType, subtypeProvider);
				if (arbitrary == null) {
					continue;
				}
				return Optional.of(arbitrary);
			}
		}
		return Optional.empty();
	}

}
