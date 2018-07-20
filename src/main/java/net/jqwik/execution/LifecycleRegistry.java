package net.jqwik.execution;

import net.jqwik.api.lifecycle.*;
import net.jqwik.descriptor.*;

public class LifecycleRegistry implements LifecycleSupplier {

	@Override
	public TeardownPropertyHook propertyFinallyLifecycle(PropertyMethodDescriptor propertyMethodDescriptor) {
		return new AutoCloseableLifecycle();
	}
}
