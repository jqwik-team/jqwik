package net.jqwik.execution.properties;

import javaslang.test.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Generate;
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
		if (optionalCreator.isPresent())
			return Optional.of((Arbitrary<Object>) invokeMethod(optionalCreator.get(), testInstance));
		return Optional.ofNullable(defaultArbitrary(genericType));
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

	private Arbitrary<Object> defaultArbitrary(GenericType forAllParameter) {
		if (forAllParameter.getRawType() == Integer.class)
			return new GenericArbitrary(Arbitrary.integer());
		if (forAllParameter.getRawType() == int.class)
			return new GenericArbitrary(Arbitrary.integer());
		return null;
	}
}


