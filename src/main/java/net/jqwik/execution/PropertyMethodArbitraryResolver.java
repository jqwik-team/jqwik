package net.jqwik.execution;

import static net.jqwik.execution.providers.DefaultArbitraryProviders.*;
import static net.jqwik.support.JqwikReflectionSupport.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.providers.*;
import net.jqwik.newArbitraries.*;
import net.jqwik.support.*;

public class PropertyMethodArbitraryResolver implements ArbitraryResolver {

	static {
		register(EnumArbitraryProvider.class);
		register(BooleanArbitraryProvider.class);
		register(IntegerArbitraryProvider.class);
		register(LongArbitraryProvider.class);
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
	public Optional<NArbitrary<Object>> forParameter(Parameter parameter) {
		Optional<ForAll> forAllAnnotation = AnnotationSupport.findAnnotation(parameter, ForAll.class);
		if (!forAllAnnotation.isPresent())
			return Optional.empty();

		String generatorName = forAllAnnotation.get().value();
		GenericType genericType = new GenericType(parameter);
		NArbitrary<?> arbitrary = forType(genericType, generatorName, parameter.getDeclaredAnnotations());
		if (arbitrary == null)
			return Optional.empty();
		else {
			NArbitrary<Object> genericArbitrary = new GenericArbitrary((NArbitrary<Object>) arbitrary);
			return Optional.of(genericArbitrary);
		}
	}

	private void configureArbitrary(NArbitrary<?> objectArbitrary, Annotation[] annotations) {
		Arrays.stream(annotations).forEach(annotation -> {
			try {
				Method configureMethod = objectArbitrary.inner().getClass().getMethod(CONFIG_METHOD_NAME, annotation.annotationType());
				JqwikReflectionSupport.invokeMethod(configureMethod, objectArbitrary.inner(), annotation);
			} catch (NoSuchMethodException ignore) {
			}
		});
	}

	private NArbitrary<?> forType(GenericType genericType, String generatorName, Annotation[] annotations) {
		NArbitrary<?> arbitrary = createForType(genericType, generatorName, annotations);
		if (arbitrary != null)
			configureArbitrary(arbitrary, annotations);
		return arbitrary;
	}

	private NArbitrary<?> createForType(GenericType genericType, String generatorName, Annotation[] annotations) {
		Optional<Method> optionalCreator = findArbitraryCreatorByName(genericType, generatorName);
		if (optionalCreator.isPresent()) {
			return (NArbitrary<?>) invokeMethod(optionalCreator.get(), testInstance);
		}

		NArbitrary<?> defaultArbitrary = findDefaultArbitrary(genericType, generatorName, annotations);
		if (defaultArbitrary != null)
			return defaultArbitrary;

		if (!generatorName.isEmpty())
			return null;

		return findFirstFitArbitrary(genericType);
	}

	private Optional<Method> findArbitraryCreatorByName(GenericType genericType, String generatorToFind) {
		if (generatorToFind.isEmpty())
			return Optional.empty();
		List<Method> creators = ReflectionSupport.findMethods(descriptor.getContainerClass(), isCreatorForType(genericType),
				HierarchyTraversalMode.BOTTOM_UP);
		return creators.stream().filter(generatorMethod -> {
			Generate generateAnnotation = generatorMethod.getDeclaredAnnotation(Generate.class);
			String generatorName = generateAnnotation.value();
			if (generatorName.isEmpty())
				generatorName = generatorMethod.getName();
			return generatorName.equals(generatorToFind);
		}).findFirst();
	}

	private NArbitrary<?> findFirstFitArbitrary(GenericType genericType) {
		Optional<Method> optionalCreator = findArbitraryCreator(genericType);
		if (optionalCreator.isPresent())
			return (NArbitrary<?>) invokeMethod(optionalCreator.get(), testInstance);
		else
			return null;
	}

	private Optional<Method> findArbitraryCreator(GenericType genericType) {
		List<Method> creators = ReflectionSupport.findMethods(descriptor.getContainerClass(), isCreatorForType(genericType),
				HierarchyTraversalMode.BOTTOM_UP);
		if (creators.size() > 1)
			throw new AmbiguousArbitraryException(genericType, creators);
		return creators.stream().findFirst();
	}

	private Predicate<Method> isCreatorForType(GenericType genericType) {
		return method -> {
			if (!method.isAnnotationPresent(Generate.class))
				return false;
			GenericType arbitraryReturnType = new GenericType(method.getAnnotatedReturnType().getType());
			if (!arbitraryReturnType.getRawType().equals(NArbitrary.class))
				return false;
			if (!arbitraryReturnType.isGeneric())
				return false;
			return genericType.isAssignableFrom(arbitraryReturnType.getTypeArguments()[0]);
		};
	}

	private NArbitrary<?> findDefaultArbitrary(GenericType parameterType, String generatorName, Annotation[] annotations) {
		Function<GenericType, NArbitrary<?>> subtypeProvider = subtype -> forType(subtype, generatorName, annotations);

		for (ArbitraryProvider provider : DefaultArbitraryProviders.getProviders()) {
			boolean generatorNameSpecified = !generatorName.isEmpty();
			if (generatorNameSpecified && !provider.needsSubtypeProvider())
				continue;
			if (provider.needsSubtypeProvider() && !(parameterType.isGeneric() || parameterType.isArray()))
				continue;
			if (provider.canProvideFor(parameterType)) {
				return provider.provideFor(parameterType, subtypeProvider);
			}
		}

		return null;
	}
}
