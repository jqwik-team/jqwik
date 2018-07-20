package net.jqwik.api.lifecycle;

import org.junit.platform.engine.*;

import java.util.concurrent.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
public interface AroundPropertyHook extends LifecycleHook {
	TestExecutionResult aroundProperty(
		PropertyLifecycleContext propertyDescriptor, Callable<TestExecutionResult> property
	) throws Throwable;
}
