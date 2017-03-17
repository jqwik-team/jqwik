package net.jqwik.execution.properties;

import javaslang.test.Arbitrary;
import net.jqwik.descriptor.PropertyMethodDescriptor;

import java.lang.reflect.Parameter;
import java.util.Optional;

public class PropertyMethodArbitraryProvider implements ArbitraryProvider {

	public PropertyMethodArbitraryProvider(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {

	}

	@Override
	public Optional<Arbitrary<Object>> forParameter(Parameter parameter) {
//		switch (parameter.getType()) {
//			case Integer.class:
//			case int.class:
//				return
//		}
		return Optional.ofNullable(defaultArbitrary(parameter.getType()));

	}

	private Arbitrary<Object> defaultArbitrary(Class<?> type) {
		if (type == Integer.class)
			return new GenericArbitrary(Arbitrary.integer());
		if (type == int.class)
			return new GenericArbitrary(Arbitrary.integer());
		return null;
	}
}
