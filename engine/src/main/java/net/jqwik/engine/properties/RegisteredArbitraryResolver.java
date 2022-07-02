package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.providers.ArbitraryProvider.*;

public class RegisteredArbitraryResolver {

	private final List<ArbitraryProvider> registeredProviders = new ArrayList<>();

	public RegisteredArbitraryResolver(Collection<ArbitraryProvider> registeredProviders) {
		this.registeredProviders.addAll(registeredProviders);
		this.registeredProviders.addAll(DefaultArbitraries.getDefaultProviders());
	}

	public Set<Arbitrary<?>> resolve(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		int currentPriority = Integer.MIN_VALUE;
		Set<Arbitrary<?>> fittingArbitraries = new LinkedHashSet<>();
		for (ArbitraryProvider provider : registeredProviders) {
			if (provider.canProvideFor(targetType)) {
				if (provider.priority() < currentPriority) {
					continue;
				}
				if (provider.priority() > currentPriority) {
					fittingArbitraries.clear();
					currentPriority = provider.priority();
				}
				Set<Arbitrary<?>> arbitraries = provider.provideFor(targetType, subtypeProvider);
				fittingArbitraries.addAll(arbitraries);
			}
		}
		return fittingArbitraries;
	}

}
