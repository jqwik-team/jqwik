package net.jqwik.execution;

import net.jqwik.api.ExampleLifecycle;
import net.jqwik.api.properties.PropertyLifecycle;
import net.jqwik.descriptor.ExampleMethodDescriptor;
import net.jqwik.descriptor.PropertyMethodDescriptor;

import java.util.function.Function;

public class LifecycleRegistry {
	public Function<Object, ExampleLifecycle> supplierFor(ExampleMethodDescriptor exampleMethodDescriptor) {
		return (testInstance) -> new AutoCloseableLifecycle();
	}

	public Function<Object, PropertyLifecycle> supplierFor(PropertyMethodDescriptor propertyMethodDescriptor) {
		return (testInstance) -> new AutoCloseableLifecycle();
	}
}
