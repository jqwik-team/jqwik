package net.jqwik.execution;

public interface PropertyLifecycle {
	void doFinally(PropertyContext propertyDescriptor, Object testInstance) throws Throwable;
}
