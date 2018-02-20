package net.jqwik.execution;

import static net.jqwik.support.JqwikReflectionSupport.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.descriptor.*;
import net.jqwik.providers.*;
import net.jqwik.support.*;

public class PropertyMethodArbitraryResolver implements ArbitraryResolver {

	private final PropertyMethodDescriptor descriptor;
	private final Object testInstance;
	private final DefaultArbitraryResolver defaultArbitraryResolver;

	public PropertyMethodArbitraryResolver(PropertyMethodDescriptor descriptor, Object testInstance) {
		this(descriptor, testInstance, new DefaultArbitraryResolver(DefaultArbitraryProviders.getProviders()));
	}

	public PropertyMethodArbitraryResolver(PropertyMethodDescriptor descriptor, Object testInstance,
			DefaultArbitraryResolver defaultArbitraryResolver) {
		this.descriptor = descriptor;
		this.testInstance = testInstance;
		this.defaultArbitraryResolver = defaultArbitraryResolver;
	}

	@Override
	public Optional<Arbitrary<Object>> forParameter(Parameter parameter) {
		Optional<ForAll> forAllAnnotation = AnnotationSupport.findAnnotation(parameter, ForAll.class);

		return forAllAnnotation.flatMap(annotation -> {
			String generatorName = forAllAnnotation.get().value();
			GenericType genericType = new GenericType(parameter);
			List<Annotation> configurationAnnotations = JqwikAnnotationSupport.findAllAnnotations(parameter) //
					.stream() //
					.filter(parameterAnnotation -> !parameterAnnotation.annotationType().equals(ForAll.class)) //
					.collect(Collectors.toList());
			return createForType(genericType, generatorName, configurationAnnotations);
		}).map(GenericArbitrary::new);

	}

	private Optional<Arbitrary<?>> createForType(GenericType genericType, String generatorName, List<Annotation> annotations) {
		Arbitrary<?> createdArbitrary = null;

		Optional<Method> optionalCreator = findArbitraryCreatorByName(genericType, generatorName);
		if (optionalCreator.isPresent()) {
			createdArbitrary = (Arbitrary<?>) invokeMethodPotentiallyOuter(optionalCreator.get(), testInstance);
		} else if (generatorName.isEmpty()) {
			createdArbitrary = resolveDefaultArbitrary(genericType, generatorName, annotations) //
					.orElseGet(() -> findFirstFitArbitrary(genericType) //
							.orElse(null));
		}

		return Optional.ofNullable(createdArbitrary);
	}

	private Optional<Method> findArbitraryCreatorByName(GenericType genericType, String generatorToFind) {
		if (generatorToFind.isEmpty())
			return Optional.empty();
		List<Method> creators = findMethodsPotentiallyOuter( //
				descriptor.getContainerClass(), //
				isCreatorForType(genericType), //
				HierarchyTraversalMode.BOTTOM_UP);
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
				HierarchyTraversalMode.BOTTOM_UP);
		if (creators.size() > 1) {
			throw new AmbiguousArbitraryException(genericType, creators);
		}
		return creators.stream().findFirst();
	}

	private Predicate<Method> isCreatorForType(GenericType genericType) {
		return method -> {
			if (!method.isAnnotationPresent(Provide.class)) {
				return false;
			}
			GenericType arbitraryReturnType = new GenericType(method.getAnnotatedReturnType().getType());
			if (!arbitraryReturnType.getRawType().equals(Arbitrary.class)) {
				return false;
			}
			if (!arbitraryReturnType.isGeneric()) {
				return false;
			}
			return genericType.isCompatibleWith(arbitraryReturnType.getTypeArguments()[0]);
		};
	}

	private Optional<Arbitrary<?>> resolveDefaultArbitrary(GenericType parameterType, String generatorName, List<Annotation> annotations) {
		boolean generatorNameSpecified = !generatorName.isEmpty();
		if (generatorNameSpecified && !parameterType.isGeneric()) {
			return Optional.empty();
		}

		Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider = subtype -> createForType(subtype, generatorName, annotations);

		return defaultArbitraryResolver.resolve(parameterType, annotations, subtypeProvider);
	}

}
