package net.jqwik.execution.properties;

import javaslang.test.Arbitrary;
import net.jqwik.api.properties.ForAll;
import net.jqwik.api.properties.Generate;
import net.jqwik.api.properties.Generator;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static net.jqwik.support.JqwikReflectionSupport.invokeMethod;

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
			GenericType arbitraryReturnType = new GenericType(method.getAnnotatedReturnType().getType());
			if (!arbitraryReturnType.getRawType().equals(Arbitrary.class))
				return false;
			if (!arbitraryReturnType.isGeneric())
				return false;
			return genericType.isAssignableFrom(arbitraryReturnType.getTypeArguments()[0]);
		};
	}

	private Arbitrary<Object> defaultArbitrary(GenericType parameterType, int size) {
		if (parameterType.isEnum()) {
			//noinspection unchecked
			return new GenericArbitrary(Generator.of((Class<Enum>) parameterType.getRawType()), size);
		}

		if (parameterType.isAssignableFrom(Integer.class))
			return new GenericArbitrary(Arbitrary.integer(), size);

		if (parameterType.isAssignableFrom(Boolean.class))
			return new GenericArbitrary(Arbitrary.of(true, false), size);

		return null;
	}
}
