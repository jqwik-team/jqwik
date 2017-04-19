package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.providers.*;
import net.jqwik.properties.*;
import org.junit.platform.commons.support.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import static net.jqwik.support.JqwikReflectionSupport.*;

public class PropertyMethodArbitraryProvider implements ArbitraryProvider {

	private final PropertyMethodDescriptor descriptor;
	private final Object testInstance;
	private final List<TypedArbitraryProvider> defaultProviders = new ArrayList<>();

	public PropertyMethodArbitraryProvider(PropertyMethodDescriptor descriptor, Object testInstance) {
		this.descriptor = descriptor;
		this.testInstance = testInstance;
		populateDefaultProviders();
	}

	private void populateDefaultProviders() {
		defaultProviders.add(new EnumArbitraryProvider());
		defaultProviders.add(new BooleanArbitraryProvider());
		defaultProviders.add(new IntegerArbitraryProvider());
		defaultProviders.add(new ListArbitraryProvider());
		defaultProviders.add(new SetArbitraryProvider());
		defaultProviders.add(new StreamArbitraryProvider());
		defaultProviders.add(new OptionalArbitraryProvider());
	}

	@Override
	public Optional<Arbitrary<Object>> forParameter(Parameter parameter) {
		Optional<ForAll> forAllAnnotation = AnnotationSupport.findAnnotation(parameter, ForAll.class);
		if (!forAllAnnotation.isPresent())
			return Optional.empty();

		String generatorName = forAllAnnotation.get().value();
		GenericType genericType = new GenericType(parameter);
		Arbitrary<?> arbitrary = forType(genericType, generatorName);
		if (arbitrary == null)
			return Optional.empty();
		else {
			Arbitrary<Object> genericArbitrary = new GenericArbitrary(arbitrary);
			return Optional.of(genericArbitrary);
		}
	}

	private Arbitrary<?> forType(GenericType genericType, String generatorName) {
		Optional<Method> optionalCreator = findArbitraryCreator(genericType, generatorName);
		if (optionalCreator.isPresent()) {
			return (Arbitrary<?>) invokeMethod(optionalCreator.get(), testInstance);
		} else {
			return defaultArbitrary(genericType, generatorName);
		}
	}

	private Optional<Method> findArbitraryCreator(GenericType genericType, String generatorToFind) {
		List<Method> creators = ReflectionSupport.findMethods(descriptor.getContainerClass(), isCreatorForType(genericType),
				HierarchyTraversalMode.BOTTOM_UP);
		return creators.stream().filter(generatorMethod -> {
			Generate generateAnnotation = generatorMethod.getDeclaredAnnotation(Generate.class);
			String generatorName = generateAnnotation.value();
			if (generatorToFind.isEmpty() && generatorName.isEmpty()) {
				return true;
			}
			if (generatorName.isEmpty())
				generatorName = generatorMethod.getName();
			return generatorName.equals(generatorToFind);
		}).findFirst();
	}

	private Predicate<Method> isCreatorForType(GenericType genericType) {
		return method -> {
			if (!method.isAnnotationPresent(Generate.class))
				return false;
			GenericType arbitraryReturnType = new GenericType(method.getAnnotatedReturnType().getType());
			if (!arbitraryReturnType.getRawType().equals(Arbitrary.class))
				return false;
			if (!arbitraryReturnType.isGeneric())
				return false;
			return genericType.isAssignableFrom(arbitraryReturnType.getTypeArguments()[0]);
		};
	}

	private Arbitrary<?> defaultArbitrary(GenericType parameterType, String generatorName) {
		boolean hasGeneratorName = !generatorName.isEmpty();
		Function<GenericType, Arbitrary<?>> subtypeProvider = subtype -> forType(subtype, generatorName);

		for (TypedArbitraryProvider provider : defaultProviders) {
			if (provider.canProvideFor(parameterType, hasGeneratorName)) {
				return provider.provideFor(parameterType, subtypeProvider);
			}
		}

		return null;
	}
}
