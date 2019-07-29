package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.ArbitraryProvider.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;
import static net.jqwik.engine.support.OverriddenMethodAnnotationSupport.*;

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

	PropertyMethodArbitraryResolver(
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
		Optional<String> optionalForAllValue =
			targetType
				.findAnnotation(ForAll.class)
				.map(ForAll::value).filter(name -> !name.equals(ForAll.NO_VALUE));

		Optional<String> optionalFromValue =
			targetType
				.findAnnotation(From.class)
				.map(From::value);

		if (optionalForAllValue.isPresent() && optionalFromValue.isPresent()) {
			String message = String.format(
				"You cannot have both @ForAll(\"%s\") and @From(\"%s\") in parameter %s",
				optionalForAllValue.get(),
				optionalFromValue.get(),
				targetType
			);
			throw new JqwikException(message);
		}

		String generatorName = optionalForAllValue.orElseGet(() -> optionalFromValue.orElse(ForAll.NO_VALUE));
		final Set<Arbitrary<?>> resolvedArbitraries =
			findArbitraryGeneratorByName(targetType, generatorName)
				.map(providerMethod -> invokeProviderMethod(providerMethod, targetType))
				.orElseGet(() -> generatorName.equals(ForAll.NO_VALUE)
									 ? resolveRegisteredArbitrary(targetType)
									 : Collections.emptySet());

		return resolvedArbitraries.stream().map(arbitrary -> configure(arbitrary, targetType)).collect(Collectors.toSet());
	}

	private Set<Arbitrary<?>> invokeProviderMethod(Method providerMethod, TypeUsage targetType) {
		Parameter[] parameters = providerMethod.getParameters();
		if (parameters.length == 0) {
			return wrapInSet(invokeMethodPotentiallyOuter(providerMethod, testInstance));
		}
		if (parameters[0].getType().isAssignableFrom(TypeUsage.class)) {
			if (parameters.length == 1) {
				return wrapInSet(invokeMethodPotentiallyOuter(providerMethod, testInstance, targetType));
			}
		}
		if (parameters[1].getType().isAssignableFrom(SubtypeProvider.class)) {
			if (parameters.length == 2) {
				SubtypeProvider subtypeProvider = this::createForType;
				return wrapInSet(invokeMethodPotentiallyOuter(providerMethod, testInstance, targetType, subtypeProvider));
			}
		}
		String message = String.format("Some of the parameters of %s are not allowed in provider methods", providerMethod);
		throw new JqwikException(message);
	}

	private Set<Arbitrary<?>> wrapInSet(Object result) {
		return Collections.singleton((Arbitrary<?>) result);
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
