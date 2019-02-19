package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.support.OverriddenMethodAnnotationSupport.*;
import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class PropertyMethodArbitraryResolver implements ArbitraryResolver {

	private final Class<?> containerClass;
	private final Object testInstance;
	private final RegisteredArbitraryResolver registeredArbitraryResolver;
	private final RegisteredArbitraryConfigurer registeredArbitraryConfigurer;

	public PropertyMethodArbitraryResolver(Class<?> containerClass, Object testInstance, DomainContext domainContext) {
		this(
			containerClass,
			testInstance,
			new RegisteredArbitraryResolver(domainContext.getArbitraryProviders()),
			new RegisteredArbitraryConfigurer(domainContext.getArbitraryConfigurators())
		);
	}

	public PropertyMethodArbitraryResolver(
		Class<?> containerClass, Object testInstance,
		RegisteredArbitraryResolver registeredArbitraryResolver,
		RegisteredArbitraryConfigurer registeredArbitraryConfigurer
	) {
		this.containerClass = containerClass;
		this.testInstance = testInstance;
		this.registeredArbitraryResolver = registeredArbitraryResolver;
		this.registeredArbitraryConfigurer = registeredArbitraryConfigurer;
	}

	@Override
	public Set<Arbitrary<?>> forParameter(MethodParameter parameter) {
		TypeUsage typeUsage = TypeUsageImpl.forParameter(parameter);
		return createForType(typeUsage);
	}

	private Set<Arbitrary<?>> createForType(TypeUsage targetType) {
		final Set<Arbitrary<?>> resolvedArbitraries = new HashSet<>();

		String generatorName = targetType.findAnnotation(ForAll.class).map(ForAll::value).orElse("");
		Optional<Method> optionalCreator = findArbitraryGeneratorByName(targetType, generatorName);
		if (optionalCreator.isPresent()) {
			Arbitrary<?> createdArbitrary = (Arbitrary<?>) invokeMethodPotentiallyOuter(optionalCreator.get(), testInstance);
			resolvedArbitraries.add(createdArbitrary);
		} else if (generatorName.isEmpty()) {
			resolvedArbitraries.addAll(resolveRegisteredArbitrary(targetType));
		}

		return resolvedArbitraries.stream().map(arbitrary -> configure(arbitrary, targetType)).collect(Collectors.toSet());
	}

	private Arbitrary<?> configure(Arbitrary<?> createdArbitrary, TypeUsage targetType) {
		return registeredArbitraryConfigurer.configure(createdArbitrary, targetType);
	}

	private Optional<Method> findArbitraryGeneratorByName(TypeUsage typeUsage, String generatorToFind) {
		if (generatorToFind.isEmpty())
			return Optional.empty();

		Function<Method, String> generatorNameSupplier = method -> {
			Optional<Provide> provideAnnotation = findDeclaredOrInheritedAnnotation(method, Provide.class);
			return provideAnnotation.map(Provide::value).orElse("");
		};
		TypeUsage targetArbitraryType = TypeUsage.of(Arbitrary.class, typeUsage);

		return findGeneratorMethod(generatorToFind, this.containerClass, Provide.class, generatorNameSupplier, targetArbitraryType);
	}

	private Set<Arbitrary<?>> resolveRegisteredArbitrary(TypeUsage parameterType) {
		return registeredArbitraryResolver.resolve(parameterType, this::createForType);
	}

}
