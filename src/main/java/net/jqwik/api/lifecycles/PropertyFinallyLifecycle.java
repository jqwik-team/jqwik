package net.jqwik.api.lifecycles;

public interface PropertyFinallyLifecycle {
	void finallyAfterProperty(PropertyLifecycleContext propertyDescriptor) throws Throwable;
}
