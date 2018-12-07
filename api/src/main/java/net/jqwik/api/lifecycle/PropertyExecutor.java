package net.jqwik.api.lifecycle;

/**
 * Experimental feature. Not ready for public usage yet.
 */
public interface PropertyExecutor {

	PropertyExecutionResult execute() throws Throwable;
}
