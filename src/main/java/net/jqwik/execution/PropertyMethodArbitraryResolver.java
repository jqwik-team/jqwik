package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.providers.*;
import net.jqwik.support.*;
import org.junit.platform.commons.support.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import static net.jqwik.execution.providers.DefaultArbitraryProviders.*;
import static net.jqwik.support.JqwikReflectionSupport.*;
import static org.junit.platform.commons.support.ReflectionSupport.*;

public class PropertyMethodArbitraryResolver implements ArbitraryResolver {

	static {
		register(EnumArbitraryProvider.class);
		register(CharacterArbitraryProvider.class);
		register(BooleanArbitraryProvider.class);
		register(IntegerArbitraryProvider.class);
		register(LongArbitraryProvider.class);
		register(DoubleArbitraryProvider.class);
		register(FloatArbitraryProvider.class);
		register(BigIntegerArbitraryProvider.class);
		register(BigDecimalArbitraryProvider.class);
		register(StringArbitraryProvider.class);
		register(ListArbitraryProvider.class);
		register(SetArbitraryProvider.class);
		register(StreamArbitraryProvider.class);
		register(OptionalArbitraryProvider.class);
		register(ArrayArbitraryProvider.class);
	}

	private final static String CONFIG_METHOD_NAME = "configure";

	private final PropertyMethodDescriptor descriptor;
	private final Object testInstance;

	public PropertyMethodArbitraryResolver(PropertyMethodDescriptor descriptor, Object testInstance) {
		this.descriptor = descriptor;
		this.testInstance = testInstance;
	}

	@Override
	public Optional<Arbitrary<Object>> forParameter(Parameter parameter) {
		Optional<ForAll> forAllAnnotation = AnnotationSupport.findAnnotation(parameter, ForAll.class);

		return forAllAnnotation.flatMap(annotation -> {
			String generatorName = forAllAnnotation.get().value();
			GenericType genericType = new GenericType(parameter);
			List<Annotation> configurationAnnotations = JqwikAnnotationSupport.findAllAnnotations(parameter);
			return forType(genericType, generatorName, configurationAnnotations);
		}).map(GenericArbitrary::new);

	}

	private void configureArbitrary(Arbitrary<?> objectArbitrary, List<Annotation> annotations) {
		annotations.forEach(annotation -> {
			try {
				Method configureMethod = objectArbitrary.inner().getClass().getMethod(CONFIG_METHOD_NAME, annotation.annotationType());
				invokeMethod(configureMethod, objectArbitrary.inner(), annotation);
			} catch (NoSuchMethodException ignore) {
			}
		});
	}

	private Optional<Arbitrary<?>> forType(GenericType genericType, String generatorName, List<Annotation> annotations) {
		Arbitrary<?> arbitrary = createForType(genericType, generatorName, annotations);
		if (arbitrary != null) {
			configureArbitrary(arbitrary, annotations);
		}
		return Optional.ofNullable(arbitrary);
	}

	// TODO: Refactor method so that it returns an Optional<Arbitrary>
	// It's not as easy as it looks, at least the code does not look nice then.
	private Arbitrary<?> createForType(GenericType genericType, String generatorName, List<Annotation> annotations) {
		Optional<Method> optionalCreator = findArbitraryCreatorByName(genericType, generatorName);
		if (optionalCreator.isPresent()) {
			return (Arbitrary<?>) invokeMethodPotentiallyOuter(optionalCreator.get(), testInstance);
		}

		if (!generatorName.isEmpty()) {
			return null;
		}

		Optional<Arbitrary<?>> defaultArbitrary = findDefaultArbitrary(genericType, generatorName, annotations);
		return defaultArbitrary //
			.orElseGet(() -> findFirstFitArbitrary(genericType) //
				.orElse(null));
	}

	private Optional<Method> findArbitraryCreatorByName(GenericType genericType, String generatorToFind) {
		if (generatorToFind.isEmpty()) return Optional.empty();
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
			return genericType.isAssignableFrom(arbitraryReturnType.getTypeArguments()[0]);
		};
	}

	private Optional<Arbitrary<?>> findDefaultArbitrary(GenericType parameterType, String generatorName, List<Annotation> annotations) {
		Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider = subtype -> forType(subtype, generatorName, annotations);

		for (ArbitraryProvider provider : DefaultArbitraryProviders.getProviders()) {
			boolean generatorNameSpecified = !generatorName.isEmpty();
			if (generatorNameSpecified && !parameterType.isGeneric()) {
				continue;
			}
			if (provider.canProvideFor(parameterType)) {
				Arbitrary<?> arbitrary = provider.provideFor(parameterType, subtypeProvider);
				if (arbitrary == null) {
					continue;
				}
				return Optional.of(arbitrary);
			}
		}

		return Optional.empty();
	}
}
