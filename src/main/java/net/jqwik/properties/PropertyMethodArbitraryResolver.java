package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.configurators.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.*;
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
		TypeUsage typeUsage = TypeUsage.forParameter(parameter);
		return createForType(typeUsage).map(GenericArbitrary::new);
	}

	private Optional<Arbitrary<?>> createForType(TypeUsage typeUsage) {
		Arbitrary<?> createdArbitrary = null;

		String generatorName = typeUsage.getAnnotation(ForAll.class).map(ForAll::value).orElse("");
		Optional<Method> optionalCreator = findArbitraryCreatorByName(typeUsage, generatorName);
		if (optionalCreator.isPresent()) {
			createdArbitrary = (Arbitrary<?>) invokeMethodPotentiallyOuter(optionalCreator.get(), testInstance);
		} else if (generatorName.isEmpty()) {
			createdArbitrary = resolveRegisteredArbitrary(typeUsage)
				.orElseGet(() -> findFirstFitArbitrary(typeUsage)
					.orElse(null));
		}

		if (createdArbitrary != null) {
			createdArbitrary = configure(createdArbitrary, typeUsage);
		}

		return Optional.ofNullable(createdArbitrary);
	}

	private Arbitrary<?> configure(Arbitrary<?> createdArbitrary, TypeUsage typeUsage) {
		List<Annotation> configurationAnnotations = findConfigurationAnnotations(typeUsage);
		if (!configurationAnnotations.isEmpty()) {
			for (ArbitraryConfigurator arbitraryConfigurator : registeredArbitraryConfigurators) {
				createdArbitrary = arbitraryConfigurator.configure(createdArbitrary, configurationAnnotations);
			}
		}
		return createdArbitrary;
	}

	private List<Annotation> findConfigurationAnnotations(TypeUsage typeUsage) {
		return typeUsage.getAnnotations() //
						.stream() //
						.filter(annotation -> !annotation.annotationType().equals(ForAll.class)) //
						.collect(Collectors.toList());
	}

	private Optional<Method> findArbitraryCreatorByName(TypeUsage typeUsage, String generatorToFind) {
		if (generatorToFind.isEmpty())
			return Optional.empty();
		List<Method> creators = findMethodsPotentiallyOuter( //
															 descriptor.getContainerClass(), //
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
		List<Method> creators = findMethodsPotentiallyOuter(descriptor.getContainerClass(), isCreatorForType(typeUsage),
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

	private Optional<Arbitrary<?>> resolveRegisteredArbitrary(TypeUsage parameterType) {
		Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider = this::createForType;

		return registeredArbitraryResolver.resolve(parameterType, subtypeProvider);
	}

}
