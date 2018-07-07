package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.configurators.*;
import net.jqwik.execution.*;
import net.jqwik.providers.*;
import net.jqwik.support.*;

import java.util.*;
import java.util.function.*;

public class ArbitraryBuilderCollector {
	private final Class<?> containerClass;
	private final RegisteredArbitraryResolver registeredArbitraryResolver;
	private final List<ArbitraryConfigurator> arbitraryConfigurators;
	private final Map<TypeUsage, List<ArbitraryBuilder>> builders = new HashMap<>();

	public ArbitraryBuilderCollector(Class<?> containerClass) {
		this(containerClass, new RegisteredArbitraryResolver(RegisteredArbitraryProviders.getProviders()),
			 RegisteredArbitraryConfigurators.getConfigurators()
		);
	}

	public ArbitraryBuilderCollector(
		Class<?> containerClass, RegisteredArbitraryResolver registeredArbitraryResolver,
		List<ArbitraryConfigurator> arbitraryConfigurators
	) {
		this.containerClass = containerClass;
		this.registeredArbitraryResolver = registeredArbitraryResolver;
		this.arbitraryConfigurators = arbitraryConfigurators;
	}

	public void collect(MethodParameter methodParameter) {
		TypeUsage type = TypeUsage.forParameter(methodParameter);
		collect(type);
	}

	private void collect(TypeUsage typeUsage) {
		Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider = ignore -> Optional.empty();
		Optional<Arbitrary<?>> arbitrary = registeredArbitraryResolver.resolve(typeUsage, subtypeProvider);
		arbitrary.ifPresent(ignore -> {
			builders.put(typeUsage, buildersForRegisteredArbitrary(typeUsage));
		});
	}

	private List<ArbitraryBuilder> buildersForRegisteredArbitrary(TypeUsage typeUsage) {
		return Collections.singletonList((testInstance, subtypeBuilders) -> {
			Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider = ignore -> Optional.empty();
			return registeredArbitraryResolver.resolve(typeUsage, subtypeProvider).get();
		});
	}

	public Map<TypeUsage, List<ArbitraryBuilder>> collectedBuilders() {
		return builders;
	}
}
