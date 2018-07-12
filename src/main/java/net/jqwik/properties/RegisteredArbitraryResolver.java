package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.util.*;
import java.util.function.*;

public class RegisteredArbitraryResolver {

	private final List<ArbitraryProvider> registeredProviders;

	public RegisteredArbitraryResolver(List<ArbitraryProvider> registeredProviders) {
		this.registeredProviders = registeredProviders;
	}

	public Set<Arbitrary<?>> resolve(TypeUsage targetType, Function<TypeUsage, Set<Arbitrary<?>>> subtypeProvider) {
		int currentPriority = Integer.MIN_VALUE;
		Set<Arbitrary<?>> fittingArbitraries = new HashSet<>();
		for (ArbitraryProvider provider : registeredProviders) {
			if (provider.canProvideFor(targetType)) {
				if (provider.priority() < currentPriority) {
					continue;
				}
				if (provider.priority() > currentPriority) {
					fittingArbitraries.clear();
					currentPriority = provider.priority();
				}
				Set<Arbitrary<?>> arbitrary = provider.provideArbitrariesFor(targetType, subtypeProvider);
				fittingArbitraries.addAll(arbitrary);
			}
		}
		return fittingArbitraries;
	}

}
