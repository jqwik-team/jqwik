package net.jqwik.api;

public interface TestLifecycle {

	void doFinally(TestContext propertyDescriptor, Object testInstance) throws Throwable;
}
