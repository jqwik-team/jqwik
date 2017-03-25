package net.jqwik.execution.properties;

import javaslang.test.Arbitrary;
import net.jqwik.api.properties.ForAll;
import net.jqwik.api.properties.Generate;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import net.jqwik.execution.properties.providers.*;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.jqwik.support.JqwikReflectionSupport.invokeMethod;

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
	}

	@Override
	public Optional<Arbitrary<Object>> forParameter(Parameter parameter) {
		ForAll forAllAnnotation = parameter.getDeclaredAnnotation(ForAll.class);
		GenericType genericType = new GenericType(parameter.getParameterizedType());
		String generatorName = forAllAnnotation.value();

		Arbitrary<?> arbitrary = forType(genericType, generatorName);

		if (arbitrary == null)
			return Optional.empty();
		else {
			int generatorSize = forAllAnnotation.size();
			Arbitrary<Object> genericArbitrary = new GenericArbitrary(arbitrary, generatorSize);
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
		List<Method> creators = ReflectionSupport.findMethods(descriptor.getContainerClass(), isCreatorForType(genericType), HierarchyTraversalMode.BOTTOM_UP);
		return creators
			.stream()
			.filter(generatorMethod -> {
				Generate generateAnnotation = generatorMethod.getDeclaredAnnotation(Generate.class);
				String generatorName = generateAnnotation.value();
				if (generatorToFind.isEmpty() && generatorName.isEmpty()) {
					return true;
				}
				if (generatorName.isEmpty())
					generatorName = generatorMethod.getName();
				return generatorName.equals(generatorToFind);
			})
			.findFirst();
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
