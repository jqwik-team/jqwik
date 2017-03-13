package net.jqwik.execution;

import net.jqwik.api.ExampleDescriptor;
import net.jqwik.api.ExampleLifecycle;

public class AutoCloseableLifecycle implements ExampleLifecycle {
	@Override
	public void doFinally(ExampleDescriptor exampleDescriptor, Object testInstance) throws Throwable {
		if (testInstance instanceof AutoCloseable) {
			AutoCloseable closeable = (AutoCloseable) testInstance;
			closeable.close();
		}
	}
}
