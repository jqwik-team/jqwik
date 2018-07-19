package net.jqwik.execution;

import net.jqwik.api.lifecycles.*;
import net.jqwik.descriptor.*;

public class LifecycleRegistry implements LifecycleSupplier {

	@Override
	public PropertyLifecycle propertyLifecycle(PropertyMethodDescriptor propertyMethodDescriptor) {
		return new AutoCloseableLifecycle();
	}
}
