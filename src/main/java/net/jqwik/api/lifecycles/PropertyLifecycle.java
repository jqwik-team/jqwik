package net.jqwik.api.lifecycles;

public interface PropertyLifecycle {
	void finallyAfterProperty(PropertyLifecycleContext propertyDescriptor) throws Throwable;
}
