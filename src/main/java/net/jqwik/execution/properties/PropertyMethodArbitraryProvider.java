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
			return typesMatch(genericReturnType.getTypeArguments()[0], genericType);
		};
	}

	private boolean typesMatch(GenericType providedType, GenericType targetType) {
		if (boxedTypeMatches(providedType.getRawType(), targetType.getRawType()))
			return true;
		return targetType.getRawType().isAssignableFrom(providedType.getRawType());
	}

	private boolean boxedTypeMatches(Class<?> providedType, Class<?> targetType) {
		if (providedType.equals(Long.class) && targetType.equals(long.class))
			return true;
		if (providedType.equals(Integer.class) && targetType.equals(int.class))
			return true;
		if (providedType.equals(Short.class) && targetType.equals(short.class))
			return true;
		if (providedType.equals(Byte.class) && targetType.equals(byte.class))
			return true;
		if (providedType.equals(Character.class) && targetType.equals(char.class))
			return true;
		if (providedType.equals(Double.class) && targetType.equals(double.class))
			return true;
		if (providedType.equals(Float.class) && targetType.equals(float.class))
			return true;
		return providedType.equals(Boolean.class) && targetType.equals(boolean.class);
	}

	private Arbitrary<Object> defaultArbitrary(GenericType parameterType, int size) {
		if (parameterType.isEnum()) {
			//noinspection unchecked
			return new GenericArbitrary(Generator.of((Class<Enum>) parameterType.getRawType()), size);
		}

		if (parameterType.getRawType() == Integer.class)
			return new GenericArbitrary(Arbitrary.integer(), size);
		if (parameterType.getRawType() == int.class)
			return new GenericArbitrary(Arbitrary.integer(), size);

		if (parameterType.getRawType() == Boolean.class)
			return new GenericArbitrary(Arbitrary.of(true, false), size);
		if (parameterType.getRawType() == boolean.class)
			return new GenericArbitrary(Arbitrary.of(true, false), size);

		return null;
	}
}
