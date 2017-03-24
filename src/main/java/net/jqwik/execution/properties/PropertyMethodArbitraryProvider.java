package net.jqwik.execution.properties;

import static net.jqwik.support.JqwikReflectionSupport.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;

import javaslang.test.*;
import net.jqwik.api.properties.*;
import net.jqwik.descriptor.*;

public class PropertyMethodArbitraryProvider implements ArbitraryProvider {

	private final PropertyMethodDescriptor descriptor;
	private final Object testInstance;

	public PropertyMethodArbitraryProvider(PropertyMethodDescriptor descriptor, Object testInstance) {
		this.descriptor = descriptor;
		this.testInstance = testInstance;
	}

	@Override
	public Optional<Arbitrary<Object>> forParameter(Parameter parameter) {
		ForAll forAllAnnotation = parameter.getDeclaredAnnotation(ForAll.class);
		GenericType genericType = new GenericType(parameter.getParameterizedType());
		Optional<Method> optionalCreator = findArbitraryCreator(genericType, forAllAnnotation.value());
		if (optionalCreator.isPresent()) {
			Arbitrary<?> arbitrary = (Arbitrary<?>) invokeMethod(optionalCreator.get(), testInstance);
			Arbitrary<Object> genericArbitrary = new GenericArbitrary(arbitrary, forAllAnnotation.size());
			return Optional.of(genericArbitrary);
		}
		if (!forAllAnnotation.value().isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(defaultArbitrary(genericType, forAllAnnotation.size()));
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
			GenericType genericReturnType = new GenericType(method.getAnnotatedReturnType().getType());
			if (!genericReturnType.isGeneric())
				return false;
			if (!genericReturnType.getRawType().equals(Arbitrary.class))
				return false;
			return genericReturnType.getTypeArguments()[0] == genericType.getRawType();
		};
	}

	private Arbitrary<Object> defaultArbitrary(GenericType parameterType, int size) {
		if (parameterType.isEnum()) {
			return new GenericArbitrary(Generator.of(parameterType.getRawType()), size);
		}
		if (parameterType.getRawType() == Integer.class)
			return new GenericArbitrary(Arbitrary.integer(), size);
		if (parameterType.getRawType() == int.class)
			return new GenericArbitrary(Arbitrary.integer(), size);
		return null;
	}
}
