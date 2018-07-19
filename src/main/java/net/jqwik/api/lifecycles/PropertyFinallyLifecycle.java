package net.jqwik.api.lifecycles;

/**
 * Experimental feature. Not ready for public usage yet.
 */
public interface PropertyFinallyLifecycle {
	void finallyAfterProperty(PropertyLifecycleContext propertyDescriptor) throws Throwable;
}
