package net.jqwik.api.properties;

public interface PropertyLifecycle {
	void doFinally(PropertyContext propertyDescriptor, Object testInstance) throws Throwable;
}
