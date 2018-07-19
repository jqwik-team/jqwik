package net.jqwik.execution;

import net.jqwik.api.lifecycles.*;
import net.jqwik.descriptor.*;

public interface LifecycleSupplier {

	PropertyFinallyLifecycle propertyFinallyLifecycle(PropertyMethodDescriptor propertyMethodDescriptor);

}
