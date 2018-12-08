package net.jqwik.engine.execution.lifecycle;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;

public interface LifecycleSupplier {

	AroundPropertyHook aroundPropertyHook(PropertyMethodDescriptor propertyMethodDescriptor);

}
