package net.jqwik.api.lifecycle;

import net.jqwik.engine.execution.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
public interface PropertyExecutor {

	PropertyExecutionResult execute() throws Throwable;
}
