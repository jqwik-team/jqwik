package net.jqwik.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.configurators.*;
import net.jqwik.providers.*;
import net.jqwik.support.*;

import static net.jqwik.support.JqwikReflectionSupport.*;

public class PropertyMethodArbitraryResolver implements ArbitraryResolver {

	private final Class<?> containerClass;
	private final Object testInstance;
	private final RegisteredArbitraryResolver registeredArbitraryResolver;
	private final RegisteredArbitraryConfigurer registeredArbitraryConfigurer;

	public PropertyMethodArbitraryResolver(Class<?> containerClass, Object testInstance) {
		this(
			containerClass,
			testInstance,
			new RegisteredArbitraryResolver(RegisteredArbitraryProviders.getProviders()),
			new RegisteredArbitraryConfigurer(RegisteredArbitraryConfigurators.getConfigurators())
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
		TypeUsage typeUsage = TypeUsage.forParameter(parameter);
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
			if (resolvedArbitraries.isEmpty()) {
				findFirstFitArbitrary(targetType).ifPresent(resolvedArbitraries::add);
			}
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
			Provide generateAnnotation = method.getDeclaredAnnotation(Provide.class);
			return generateAnnotation.value();
		};
		TypeUsage targetArbitraryType = TypeUsage.of(Arbitrary.class, typeUsage);

		return findGeneratorMethod(generatorToFind, this.containerClass, Provide.class, generatorNameSupplier, targetArbitraryType);
	}

	private Optional<Arbitrary<?>> findFirstFitArbitrary(TypeUsage typeUsage) {
		return findArbitraryCreator(typeUsage).map(creator -> (Arbitrary<?>) invokeMethodPotentiallyOuter(creator, testInstance));
	}

	private Optional<Method> findArbitraryCreator(TypeUsage typeUsage) {
		TypeUsage targetArbitraryType = TypeUsage.of(Arbitrary.class, typeUsage);
		List<Method> creators = findMethodsPotentiallyOuter(
			containerClass,
			isGeneratorMethod(targetArbitraryType, Provide.class),
			HierarchyTraversalMode.BOTTOM_UP
		);
		if (creators.size() > 1) {
			throw new AmbiguousArbitraryException(typeUsage, creators);
		}
		return creators.stream().findFirst();
	}

	private Set<Arbitrary<?>> resolveRegisteredArbitrary(TypeUsage parameterType) {
		return registeredArbitraryResolver.resolve(parameterType, this::createForType);
	}

}
