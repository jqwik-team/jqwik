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

	public Optional<Arbitrary<?>> resolve(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider) {
		// Wildcards without bounds are handled specially
		//if (targetType.isWildcard() && !targetType.hasUpperBounds() && !targetType.hasLowerBounds())
		//	return Optional.of(new WildcardArbitrary());

		List<Arbitrary<?>> fittingArbitraries = new ArrayList<>();
		for (ArbitraryProvider provider : registeredProviders) {
			if (provider.canProvideFor(targetType)) {
				Arbitrary<?> arbitrary = provider.provideFor(targetType, subtypeProvider);
				if (arbitrary != null) {
					fittingArbitraries.add(arbitrary);
				}
			}
		}
		if (fittingArbitraries.isEmpty()) {
			return Optional.empty();
		}
		// TODO: Handle case of more than one fitting arbitrary.
		return Optional.of(fittingArbitraries.get(0));

	}

}
