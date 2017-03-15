package net.jqwik.api;

public interface TestLifecycle {

	void doFinally(TestDescriptor propertyDescriptor, Object testInstance) throws Throwable;
}
