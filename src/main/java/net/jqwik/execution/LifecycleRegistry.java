package net.jqwik.execution;

import net.jqwik.api.lifecycles.*;
import net.jqwik.descriptor.*;

public class LifecycleRegistry implements LifecycleSupplier {

	@Override
	public PropertyFinallyLifecycle propertyFinallyLifecycle(PropertyMethodDescriptor propertyMethodDescriptor) {
		return new AutoCloseableLifecycle();
	}
}
