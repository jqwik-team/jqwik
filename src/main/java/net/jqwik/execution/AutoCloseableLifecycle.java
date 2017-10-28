package net.jqwik.execution;

public class AutoCloseableLifecycle implements PropertyLifecycle {
	@Override
	public void doFinally(PropertyContext exampleDescriptor, Object testInstance) throws Throwable {
		if (testInstance instanceof AutoCloseable) {
			AutoCloseable closeable = (AutoCloseable) testInstance;
			closeable.close();
		}
	}
}
