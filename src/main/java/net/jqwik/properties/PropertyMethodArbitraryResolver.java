package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.configurators.*;
import net.jqwik.providers.*;
import net.jqwik.support.*;
import org.junit.platform.commons.support.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static net.jqwik.support.JqwikReflectionSupport.*;

public class PropertyMethodArbitraryResolver implements ArbitraryResolver {

	private final Class<?> containerClass;
	private final Object testInstance;
	private final RegisteredArbitraryResolver registeredArbitraryResolver;
	private final RegisteredArbitraryConfigurer registeredArbitraryConfigurer;

	public PropertyMethodArbitraryResolver(Class<?> containerClass, Object testInstance) {
		this(containerClass, testInstance, new RegisteredArbitraryResolver(RegisteredArbitraryProviders.getProviders()),
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

	private Set<Arbitrary<?>> createForType(TypeUsage typeUsage) {
		final Set<Arbitrary<?>> resolvedArbitraries = new HashSet<>();

		String generatorName = typeUsage.getAnnotation(ForAll.class).map(ForAll::value).orElse("");
		Optional<Method> optionalCreator = findArbitraryCreatorByName(typeUsage, generatorName);
		if (optionalCreator.isPresent()) {
			Arbitrary<?> createdArbitrary = (Arbitrary<?>) invokeMethodPotentiallyOuter(optionalCreator.get(), testInstance);
			resolvedArbitraries.add(createdArbitrary);
		} else if (generatorName.isEmpty()) {
			resolvedArbitraries.addAll(resolveRegisteredArbitrary(typeUsage));
			if (resolvedArbitraries.isEmpty()) {
				findFirstFitArbitrary(typeUsage).ifPresent(resolvedArbitraries::add);
			}
		}

		return resolvedArbitraries.stream().map(arbitrary -> configure(arbitrary, typeUsage)).collect(Collectors.toSet());
	}

	private Arbitrary<?> configure(Arbitrary<?> createdArbitrary, TypeUsage typeUsage) {
		return registeredArbitraryConfigurer.configure(createdArbitrary, typeUsage.getAnnotations());
	}

	private Optional<Method> findArbitraryCreatorByName(TypeUsage typeUsage, String generatorToFind) {
		if (generatorToFind.isEmpty())
			return Optional.empty();
		List<Method> creators = findMethodsPotentiallyOuter( //
															 containerClass, //
															 isCreatorForType(typeUsage), //
															 HierarchyTraversalMode.BOTTOM_UP
		);
		return creators.stream().filter(generatorMethod -> {
			Provide generateAnnotation = generatorMethod.getDeclaredAnnotation(Provide.class);
			String generatorName = generateAnnotation.value();
			if (generatorName.isEmpty()) {
				generatorName = generatorMethod.getName();
			}
			return generatorName.equals(generatorToFind);
		}).findFirst();
	}

	private Optional<Arbitrary<?>> findFirstFitArbitrary(TypeUsage typeUsage) {
		return findArbitraryCreator(typeUsage).map(creator -> (Arbitrary<?>) invokeMethodPotentiallyOuter(creator, testInstance));
	}

	private Optional<Method> findArbitraryCreator(TypeUsage typeUsage) {
		List<Method> creators = findMethodsPotentiallyOuter(containerClass, isCreatorForType(typeUsage),
															HierarchyTraversalMode.BOTTOM_UP
		);
		if (creators.size() > 1) {
			throw new AmbiguousArbitraryException(typeUsage, creators);
		}
		return creators.stream().findFirst();
	}

	private Predicate<Method> isCreatorForType(TypeUsage targetType) {
		return method -> {
			if (!method.isAnnotationPresent(Provide.class)) {
				return false;
			}
			TypeUsage arbitraryReturnType = TypeUsage.forType(method.getAnnotatedReturnType().getType());
			TypeUsage targetArbitraryType = TypeUsage.of(Arbitrary.class, targetType);
			return arbitraryReturnType.canBeAssignedTo(targetArbitraryType);
		};
	}

	private Set<Arbitrary<?>> resolveRegisteredArbitrary(TypeUsage parameterType) {
		return registeredArbitraryResolver.resolve(parameterType, this::createForType);
	}

}
