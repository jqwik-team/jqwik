package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.util.*;
import java.util.function.*;

public class RegisteredArbitraryResolver {

	private final List<ArbitraryProvider> registeredProviders;

	public RegisteredArbitraryResolver(List<ArbitraryProvider> registeredProviders) {
		this.registeredProviders = registeredProviders;
	}

	public List<Arbitrary<?>> resolve(TypeUsage targetType, Function<TypeUsage, List<Arbitrary<?>>> subtypeProvider) {
		List<Arbitrary<?>> fittingArbitraries = new ArrayList<>();
		for (ArbitraryProvider provider : registeredProviders) {
			if (provider.canProvideFor(targetType)) {
				List<Arbitrary<?>> arbitrary = provider.provideArbitrariesFor(targetType, subtypeProvider);
				if (arbitrary != null) {
					fittingArbitraries.addAll(arbitrary);
				}
			}
		}
		return fittingArbitraries;
	}

}
