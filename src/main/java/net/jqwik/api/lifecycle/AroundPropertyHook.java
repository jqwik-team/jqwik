package net.jqwik.api.lifecycle;

import org.junit.platform.engine.*;

import java.util.concurrent.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
public interface AroundPropertyHook extends LifecycleHook {
	AroundPropertyHook NONE = (propertyDescriptor, property) -> property.call();

	TestExecutionResult aroundProperty(PropertyLifecycleContext context, Callable<TestExecutionResult> property) throws Throwable;
}
