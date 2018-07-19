package net.jqwik.api.lifecycles;

import org.junit.platform.engine.*;

import java.util.concurrent.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
public interface PropertyAroundLifecycle {
	TestExecutionResult aroundProperty(
		PropertyLifecycleContext propertyDescriptor, Callable<TestExecutionResult> property
	) throws Throwable;
}
