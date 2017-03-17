package net.jqwik.execution.properties;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import javaslang.test.Arbitrary;
import net.jqwik.descriptor.PropertyMethodDescriptor;

public class PropertyMethodArbitraryProvider implements ArbitraryProvider {

	public PropertyMethodArbitraryProvider(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {

	}

	@Override
	public Optional<Arbitrary<Object>> forParameter(Parameter parameter) {
		// switch (parameter.getType()) {
		// case Integer.class:
		// case int.class:
		// return
		// }
		Type parameterizedType = parameter.getParameterizedType();
		return Optional.ofNullable(defaultArbitrary(parameterizedType));

	}

	private Arbitrary<Object> defaultArbitrary(Type parameterizedType) {
		Class rawType = null;
		if (parameterizedType instanceof  Class) {
			rawType = (Class) parameterizedType;
		}
		if (rawType == Integer.class)
			return new GenericArbitrary(Arbitrary.integer());
		if (rawType == int.class)
			return new GenericArbitrary(Arbitrary.integer());
		// if (parameterizedType.getRawType() == List.class && parameterizedType.getActualTypeArguments()[0] ==
		// Integer.class) {
		// Arbitrary<javaslang.collection.List<Integer>> typedArbitrary = Arbitrary.list(Arbitrary.integer());
		// return new GenericArbitrary(typedArbitrary);
		// }
		return null;
	}
}
