package net.jqwik.api.lifecycle;

import org.junit.platform.engine.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
public interface PropertyExecutor {

	TestExecutionResult execute() throws Throwable;
}
