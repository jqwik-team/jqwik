package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.configurators.*;
import net.jqwik.descriptor.*;
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

	private final PropertyMethodDescriptor descriptor;
	private final Object testInstance;
	private final RegisteredArbitraryResolver registeredArbitraryResolver;
	private final List<ArbitraryConfigurator> registeredArbitraryConfigurators;

	public PropertyMethodArbitraryResolver(PropertyMethodDescriptor descriptor, Object testInstance) {
		this(descriptor, testInstance, new RegisteredArbitraryResolver(RegisteredArbitraryProviders.getProviders()),
			 RegisteredArbitraryConfigurators.getConfigurators()
		);
	}

	public PropertyMethodArbitraryResolver(
		PropertyMethodDescriptor descriptor, Object testInstance,
		RegisteredArbitraryResolver registeredArbitraryResolver,
		List<ArbitraryConfigurator> registeredArbitraryConfigurators
	) {
		this.descriptor = descriptor;
		this.testInstance = testInstance;
		this.registeredArbitraryResolver = registeredArbitraryResolver;
		this.registeredArbitraryConfigurators = registeredArbitraryConfigurators;
	}

	@Override
	public Optional<Arbitrary<Object>> forParameter(MethodParameter parameter) {
		if (!parameter.isAnnotated(ForAll.class)) {
			return Optional.empty();
		}
		GenericType genericType = GenericType.forParameter(parameter);
		return createForType(genericType).map(GenericArbitrary::new);
	}

	private Optional<Arbitrary<?>> createForType(GenericType genericType) {
		Arbitrary<?> createdArbitrary = null;

		String generatorName = genericType.getAnnotation(ForAll.class).map(ForAll::value).orElse("");
		Optional<Method> optionalCreator = findArbitraryCreatorByName(genericType, generatorName);
		if (optionalCreator.isPresent()) {
			createdArbitrary = (Arbitrary<?>) invokeMethodPotentiallyOuter(optionalCreator.get(), testInstance);
		} else if (generatorName.isEmpty()) {
			createdArbitrary = resolveDefaultArbitrary(genericType)
				.orElseGet(() -> findFirstFitArbitrary(genericType)
					.orElse(null));
		}

		if (createdArbitrary != null) {
			createdArbitrary = configure(createdArbitrary, genericType);
		}

		return Optional.ofNullable(createdArbitrary);
	}

	private Arbitrary<?> configure(Arbitrary<?> createdArbitrary, GenericType genericType) {
		List<Annotation> configurationAnnotations = findConfigurationAnnotations(genericType);
		if (!configurationAnnotations.isEmpty()) {
			for (ArbitraryConfigurator arbitraryConfigurator : registeredArbitraryConfigurators) {
				createdArbitrary = arbitraryConfigurator.configure(createdArbitrary, configurationAnnotations);
			}
		}
		return createdArbitrary;
	}

	private List<Annotation> findConfigurationAnnotations(GenericType genericType) {
		return genericType.getAnnotations() //
						  .stream() //
						  .filter(annotation -> !annotation.annotationType().equals(ForAll.class)) //
						  .collect(Collectors.toList());
	}

	private Optional<Method> findArbitraryCreatorByName(GenericType genericType, String generatorToFind) {
		if (generatorToFind.isEmpty())
			return Optional.empty();
		List<Method> creators = findMethodsPotentiallyOuter( //
															 descriptor.getContainerClass(), //
															 isCreatorForType(genericType), //
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

	private Optional<Arbitrary<?>> findFirstFitArbitrary(GenericType genericType) {
		return findArbitraryCreator(genericType) //
												 .map(creator -> (Arbitrary<?>) invokeMethodPotentiallyOuter(creator, testInstance));
	}

	private Optional<Method> findArbitraryCreator(GenericType genericType) {
		List<Method> creators = findMethodsPotentiallyOuter(descriptor.getContainerClass(), isCreatorForType(genericType),
															HierarchyTraversalMode.BOTTOM_UP
		);
		if (creators.size() > 1) {
			throw new AmbiguousArbitraryException(genericType, creators);
		}
		return creators.stream().findFirst();
	}

	private Predicate<Method> isCreatorForType(GenericType targetType) {
		return method -> {
			if (!method.isAnnotationPresent(Provide.class)) {
				return false;
			}
			GenericType arbitraryReturnType = GenericType.forType(method.getAnnotatedReturnType().getType());
			GenericType targetArbitraryType = GenericType.of(Arbitrary.class, targetType);
			return arbitraryReturnType.canBeAssignedTo(targetArbitraryType);
		};
	}

	private Optional<Arbitrary<?>> resolveDefaultArbitrary(GenericType parameterType) {
		Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider = this::createForType;

		return registeredArbitraryResolver.resolve(parameterType, subtypeProvider);
	}

}
