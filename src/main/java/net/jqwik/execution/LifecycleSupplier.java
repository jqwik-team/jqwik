package net.jqwik.execution;

import net.jqwik.api.lifecycles.*;
import net.jqwik.descriptor.*;

public interface LifecycleSupplier {

	PropertyLifecycle propertyLifecycle(PropertyMethodDescriptor propertyMethodDescriptor);

}
