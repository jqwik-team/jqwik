package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.providers.ArbitraryProvider.*;
import net.jqwik.engine.properties.arbitraries.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

class RegisteredArbitraryResolverTests {

	@Example
	void noArbitraryFits() {
		List<ArbitraryProvider> providers = asList(
			createProvider(TypeUsage.of(Integer.class), 0, new DefaultIntegerArbitrary())
		);
		RegisteredArbitraryResolver resolver = new RegisteredArbitraryResolver(providers);

		Set<Arbitrary<?>> arbitraries = resolver.resolve(TypeUsage.of(String.class), noSubtypes());
		assertThat(arbitraries).isEmpty();
	}

	@Example
	void singleArbitraryFits() {
		DefaultIntegerArbitrary defaultIntegerArbitrary = new DefaultIntegerArbitrary();
		List<ArbitraryProvider> providers = asList(
			createProvider(TypeUsage.of(Integer.class), 0, defaultIntegerArbitrary)
		);
		RegisteredArbitraryResolver resolver = new RegisteredArbitraryResolver(providers);

		Set<Arbitrary<?>> arbitraries = resolver.resolve(TypeUsage.of(Integer.class), noSubtypes());
		assertThat(arbitraries).containsOnly(defaultIntegerArbitrary);
	}

	@Example
	void twoArbitrariesFromTwoProviders() {
		Arbitrary<?> defaultIntegerArbitrary = new DefaultIntegerArbitrary();
		Arbitrary<?>  defaultDoubleArbitrary = new DefaultDoubleArbitrary();
		List<ArbitraryProvider> providers = asList(
			createProvider(TypeUsage.of(Integer.class), 0, defaultIntegerArbitrary),
			createProvider(TypeUsage.of(Double.class), 0, defaultDoubleArbitrary)
		);
		RegisteredArbitraryResolver resolver = new RegisteredArbitraryResolver(providers);

		Set<Arbitrary<?>> arbitraries = resolver.resolve(TypeUsage.of(Number.class), noSubtypes());
		assertThat(arbitraries).containsOnly(defaultIntegerArbitrary, defaultDoubleArbitrary);
	}

	@Example
	void twoArbitrariesFromOneProvider() {
		Arbitrary<?> defaultIntegerArbitrary = new DefaultIntegerArbitrary();
		Arbitrary<?>  defaultDoubleArbitrary = new DefaultDoubleArbitrary();
		List<ArbitraryProvider> providers = asList(
			createProvider(TypeUsage.of(Number.class), 0, defaultIntegerArbitrary, defaultDoubleArbitrary)
		);
		RegisteredArbitraryResolver resolver = new RegisteredArbitraryResolver(providers);

		Set<Arbitrary<?>> arbitraries = resolver.resolve(TypeUsage.of(Number.class), noSubtypes());
		assertThat(arbitraries).containsOnly(defaultIntegerArbitrary, defaultDoubleArbitrary);
	}

	@Example
	void higherPriorityTrumpsLowerPriority() {
		Arbitrary<?> defaultIntegerArbitrary = new DefaultIntegerArbitrary();
		Arbitrary<?>  defaultDoubleArbitrary = new DefaultDoubleArbitrary();
		Arbitrary<?>  higherPriorityArbitrary = new DefaultLongArbitrary();
		List<ArbitraryProvider> providers = asList(
			createProvider(TypeUsage.of(Integer.class), 0, defaultIntegerArbitrary),
			createProvider(TypeUsage.of(Double.class), 0, defaultDoubleArbitrary),
			createProvider(TypeUsage.of(Long.class), 1, higherPriorityArbitrary)
		);
		RegisteredArbitraryResolver resolver = new RegisteredArbitraryResolver(providers);

		Set<Arbitrary<?>> arbitraries = resolver.resolve(TypeUsage.of(Number.class), noSubtypes());
		assertThat(arbitraries).containsOnly(higherPriorityArbitrary);
	}

	@Example
	void providersCanBeEvaluatedInAnyOrder() {
		Arbitrary<?> defaultIntegerArbitrary = new DefaultIntegerArbitrary();
		Arbitrary<?>  defaultDoubleArbitrary = new DefaultDoubleArbitrary();
		Arbitrary<?>  higherPriorityArbitrary = new DefaultLongArbitrary();
		Arbitrary<?>  highestPriorityArbitrary1 = new DefaultFloatArbitrary();
		Arbitrary<?>  highestPriorityArbitrary2 = new DefaultShortArbitrary();
		Arbitrary<?>  highestPriorityArbitrary3 = new DefaultByteArbitrary();
		List<ArbitraryProvider> providers = asList(
			createProvider(TypeUsage.of(Integer.class), 0, defaultIntegerArbitrary),
			createProvider(TypeUsage.of(Float.class), 2, highestPriorityArbitrary1),
			createProvider(TypeUsage.of(Double.class), 0, defaultDoubleArbitrary),
			createProvider(TypeUsage.of(Short.class), 2, highestPriorityArbitrary2, highestPriorityArbitrary3),
			createProvider(TypeUsage.of(Number.class), 1, higherPriorityArbitrary)
		);
		RegisteredArbitraryResolver resolver = new RegisteredArbitraryResolver(providers);

		Set<Arbitrary<?>> arbitraries = resolver.resolve(TypeUsage.of(Number.class), noSubtypes());
		assertThat(arbitraries).containsOnly(highestPriorityArbitrary1, highestPriorityArbitrary2, highestPriorityArbitrary3);
	}

	private SubtypeProvider noSubtypes() {
		return ignore -> Collections.emptySet();
	}

	private ArbitraryProvider createProvider(TypeUsage typeUsage, int priority, Arbitrary<?>... arbitraries) {
		return new ArbitraryProvider() {
			@Override
			public boolean canProvideFor(TypeUsage targetType) {
				return typeUsage.canBeAssignedTo(targetType);
			}

			@Override
			public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
				return new LinkedHashSet<>(asList(arbitraries));
			}

			@Override
			public int priority() {
				return priority;
			}
		};
	}
}
