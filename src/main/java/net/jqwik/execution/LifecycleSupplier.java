package net.jqwik.execution;

import net.jqwik.api.lifecycle.*;
import net.jqwik.descriptor.*;

public interface LifecycleSupplier {

	TeardownPropertyHook propertyFinallyLifecycle(PropertyMethodDescriptor propertyMethodDescriptor);

}
