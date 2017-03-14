package net.jqwik.execution;

import net.jqwik.api.ExampleLifecycle;
import net.jqwik.descriptor.ExampleMethodDescriptor;

public class LifecycleRegistry {
	public ExampleLifecycle lifecycleFor(ExampleMethodDescriptor exampleMethodDescriptor) {
		return new AutoCloseableLifecycle();
	}
}
