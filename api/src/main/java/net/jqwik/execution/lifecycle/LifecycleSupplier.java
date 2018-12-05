package net.jqwik.execution.lifecycle;

import net.jqwik.api.lifecycle.*;
import net.jqwik.descriptor.*;

public interface LifecycleSupplier {

	AroundPropertyHook aroundPropertyHook(PropertyMethodDescriptor propertyMethodDescriptor);

}
