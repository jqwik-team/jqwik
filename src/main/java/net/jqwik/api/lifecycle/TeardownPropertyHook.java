package net.jqwik.api.lifecycle;

/**
 * Experimental feature. Not ready for public usage yet.
 */
public interface TeardownPropertyHook extends LifecycleHook {
	void teardownProperty(PropertyLifecycleContext propertyDescriptor) throws Throwable;
}
