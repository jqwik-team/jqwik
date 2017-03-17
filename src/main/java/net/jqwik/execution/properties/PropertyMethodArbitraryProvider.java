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
		// Todo: Find correct arbitrary for type
		return Optional.of(new GenericArbitrary(Arbitrary.integer()));

	}
}
