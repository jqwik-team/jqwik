package net.jqwik.execution;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.properties.*;
import net.jqwik.descriptor.*;

public class LifecycleRegistry {
	public Function<Object, ExampleLifecycle> supplierFor(ExampleMethodDescriptor exampleMethodDescriptor) {
		return (testInstance) -> new AutoCloseableLifecycle();
	}

	public Function<Object, PropertyLifecycle> supplierFor(PropertyMethodDescriptor propertyMethodDescriptor) {
		return (testInstance) -> new AutoCloseableLifecycle();
	}
}
