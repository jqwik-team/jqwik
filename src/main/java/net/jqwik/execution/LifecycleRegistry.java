package net.jqwik.execution;

import net.jqwik.api.properties.*;
import net.jqwik.descriptor.*;

import java.util.function.*;

public class LifecycleRegistry {

	public Function<Object, PropertyLifecycle> supplierFor(PropertyMethodDescriptor propertyMethodDescriptor) {
		return (testInstance) -> new AutoCloseableLifecycle();
	}
}
