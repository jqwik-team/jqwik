package net.jqwik.execution;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

public class RegisteredArbitraryResolver {

	private final List<ArbitraryProvider> registeredProviders;

	public RegisteredArbitraryResolver(List<ArbitraryProvider> registeredProviders) {
		this.registeredProviders = registeredProviders;
	}

	public Optional<Arbitrary<?>> resolve(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider) {
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
