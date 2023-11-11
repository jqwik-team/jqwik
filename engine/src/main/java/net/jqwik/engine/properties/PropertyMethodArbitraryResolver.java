package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;
import net.jqwik.engine.support.types.*;

public class PropertyMethodArbitraryResolver extends InstanceBasedSubtypeProvider implements ArbitraryResolver {

	private final RegisteredArbitraryResolver registeredArbitraryResolver;
	private final RegisteredArbitraryConfigurer registeredArbitraryConfigurer;

	public PropertyMethodArbitraryResolver(List<Object> testInstances, DomainContext domainContext) {
		this(
			testInstances,
			new RegisteredArbitraryResolver(domainContext.getArbitraryProviders()),
			new RegisteredArbitraryConfigurer(domainContext.getArbitraryConfigurators())
		);
	}

	PropertyMethodArbitraryResolver(
		List<Object> testInstances,
		RegisteredArbitraryResolver registeredArbitraryResolver,
		RegisteredArbitraryConfigurer registeredArbitraryConfigurer
	) {
		super(testInstances);
		this.registeredArbitraryResolver = registeredArbitraryResolver;
		this.registeredArbitraryConfigurer = registeredArbitraryConfigurer;
	}

	@Override
	public Set<Arbitrary<?>> forParameter(MethodParameter parameter) {
		TypeUsage typeUsage = TypeUsageImpl.forParameter(parameter);
		return apply(typeUsage);
	}

	@Override
	protected Set<Arbitrary<?>> resolve(TypeUsage targetType) {
		return registeredArbitraryResolver.resolve(targetType, this);
	}

	@Override
	protected Arbitrary<?> configure(Arbitrary<?> arbitrary, TypeUsage targetType) {
		return registeredArbitraryConfigurer.configure(arbitrary, targetType);
	}

}
