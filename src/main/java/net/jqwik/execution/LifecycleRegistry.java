package net.jqwik.execution;

import java.util.function.Function;

import net.jqwik.descriptor.PropertyMethodDescriptor;

public class LifecycleRegistry {

	public Function<Object, PropertyLifecycle> supplierFor(PropertyMethodDescriptor propertyMethodDescriptor) {
		return (testInstance) -> new AutoCloseableLifecycle();
	}
}
