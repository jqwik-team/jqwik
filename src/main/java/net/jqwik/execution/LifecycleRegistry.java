package net.jqwik.execution;

import net.jqwik.api.ExampleLifecycle;
import net.jqwik.api.PropertyLifecycle;
import net.jqwik.descriptor.ExampleMethodDescriptor;
import net.jqwik.descriptor.PropertyMethodDescriptor;

public class LifecycleRegistry {
	public ExampleLifecycle lifecycleFor(ExampleMethodDescriptor exampleMethodDescriptor) {
		return new AutoCloseableLifecycle();
	}

	public PropertyLifecycle lifecycleFor(PropertyMethodDescriptor propertyMethodDescriptor) {
		return new AutoCloseableLifecycle();
	}
}
