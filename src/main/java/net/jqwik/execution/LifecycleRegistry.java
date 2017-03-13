package net.jqwik.execution;

import net.jqwik.api.ExampleLifecycle;
import net.jqwik.discovery.ExampleMethodDescriptor;

public class LifecycleRegistry {
	public ExampleLifecycle lifecycleFor(ExampleMethodDescriptor exampleMethodDescriptor) {
		return new AutoCloseableLifecycle();
	}
}
