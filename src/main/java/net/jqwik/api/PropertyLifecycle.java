package net.jqwik.api;

public interface PropertyLifecycle {
	void doFinally(PropertyContext propertyDescriptor, Object testInstance) throws Throwable;
}
