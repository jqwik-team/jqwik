package net.jqwik.engine.execution.lifecycle;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;

public interface LifecycleHooksSupplier {

	AroundPropertyHook aroundPropertyHook(PropertyMethodDescriptor propertyMethodDescriptor);

	AroundTryHook aroundTryHook(PropertyMethodDescriptor methodDescriptor);

	BeforeContainerHook beforeContainerHook(TestDescriptor descriptor);

	AfterContainerHook afterContainerHook(TestDescriptor descriptor);

	ResolveParameterHook resolveParameterHook(TestDescriptor descriptor);

	SkipExecutionHook skipExecutionHook(TestDescriptor testDescriptor);

	InvokePropertyMethodHook invokePropertyMethodHook(TestDescriptor testDescriptor);

	ProvidePropertyInstanceHook providePropertyInstanceHook(TestDescriptor testDescriptor);
}
