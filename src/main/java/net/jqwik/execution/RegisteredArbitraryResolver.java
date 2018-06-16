package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.properties.arbitraries.*;

import java.lang.annotation.*;
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
		return Optional.of(fittingArbitraries.get(0));

		// TODO: Arbitrary should be fixed for one property try
		//		if (fittingArbitraries.size() == 1) {
		//			return Optional.of(fittingArbitraries.get(0));
		//		}
		//		return Optional.of(new RegisteredArbitraries(fittingArbitraries));
	}

	private static class RegisteredArbitraries<T> implements Arbitrary<T>, Configurable<T> {
		private final List<Arbitrary<T>> arbitraries = new ArrayList<>();

		public RegisteredArbitraries(List<Arbitrary<T>> arbitraries) {
			// The list must not be immutable
			this.arbitraries.addAll(arbitraries);
		}

		@Override
		public RandomGenerator<T> generator(int genSize) {
			return RandomGenerators.choose(arbitraries).flatMap(Function.identity(), genSize);
		}

		@Override
		public Arbitrary<T> configure(ArbitraryConfigurator configurator, List<Annotation> annotations) {
			for (int i = 0; i < arbitraries.size(); i++) {
				Arbitrary<T> focus = arbitraries.get(i);
				arbitraries.set(i, configurator.configure(focus, annotations));
			}
			return this;
		}
	}
}
