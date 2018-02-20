package net.jqwik.execution;

import static net.jqwik.support.JqwikReflectionSupport.*;
import static org.junit.platform.commons.support.ReflectionSupport.*;

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

	private final static String CONFIG_METHOD_NAME = "configure";

	private final PropertyMethodDescriptor descriptor;
	private final Object testInstance;
	private final List<ArbitraryProvider> defaultProviders;

	public PropertyMethodArbitraryResolver(PropertyMethodDescriptor descriptor, Object testInstance) {
		this(descriptor, testInstance, DefaultArbitraryProviders.getProviders());
	}

	public PropertyMethodArbitraryResolver(PropertyMethodDescriptor descriptor, Object testInstance,
			List<ArbitraryProvider> defaultProviders) {
		this.descriptor = descriptor;
		this.testInstance = testInstance;
		this.defaultProviders = defaultProviders;
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
			createdArbitrary = findDefaultArbitrary(genericType, generatorName, annotations) //
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

	@SuppressWarnings("unchecked")
	private Optional<Arbitrary<?>> findDefaultArbitrary(GenericType parameterType, String generatorName, List<Annotation> annotations) {
		Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider = subtype -> createForType(subtype, generatorName, annotations);

		for (ArbitraryProvider provider : defaultProviders) {
			boolean generatorNameSpecified = !generatorName.isEmpty();
			if (generatorNameSpecified && !parameterType.isGeneric()) {
				continue;
			}
			if (provider.canProvideFor(parameterType)) {
				Arbitrary<?> arbitrary = provider.provideFor(parameterType, subtypeProvider);
				if (arbitrary == null) {
					continue;
				}
				arbitrary = configureArbitraryInProvider(arbitrary, provider, annotations);
				return Optional.of(arbitrary);
			}
		}

		return Optional.empty();
	}

	private Arbitrary<?> configureArbitraryInProvider(Arbitrary<?> arbitrary, ArbitraryProvider provider, List<Annotation> annotations) {
		for (Annotation annotation : annotations) {
			Class<? extends Arbitrary> arbitraryClass = arbitrary.getClass();
			Optional<Method> configurationMethod = JqwikReflectionSupport.findMethod(provider.getClass(),
					method -> hasCompatibleConfigurationSignature(method, arbitraryClass, annotation), HierarchyTraversalMode.BOTTOM_UP);
			if (configurationMethod.isPresent()) {
				arbitrary = (Arbitrary<?>) invokeMethod(configurationMethod.get(), provider, arbitrary, annotation);
			}
		}
		return arbitrary;
	}

	private static boolean hasCompatibleConfigurationSignature(Method candidate, Class<? extends Arbitrary> arbitraryClass,
			Annotation annotation) {
		if (!CONFIG_METHOD_NAME.equals(candidate.getName())) {
			return false;
		}
		if (!Arbitrary.class.isAssignableFrom(candidate.getReturnType())) {
			return false;
		}
		if (candidate.getParameterCount() != 2) {
			return false;
		}
		if (candidate.getParameterTypes()[1] != annotation.annotationType()) {
			return false;
		}
		Class<?> upperArbitraryType = candidate.getParameterTypes()[0];
		return upperArbitraryType.isAssignableFrom(arbitraryClass);
	}

}
