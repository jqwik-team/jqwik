package net.jqwik.engine.execution.lifecycle;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;

public interface LifecycleHooksSupplier {

	AroundPropertyHook aroundPropertyHook(PropertyMethodDescriptor propertyMethodDescriptor);

	SkipExecutionHook skipExecutionHook(TestDescriptor testDescriptor);
}
