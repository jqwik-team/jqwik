package net.jqwik.api;

public interface ExampleLifecycle {

	void doFinally(ExampleDescriptor exampleDescriptor, Object testInstance) throws Throwable;
}
