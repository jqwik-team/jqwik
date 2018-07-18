package net.jqwik.execution;

import net.jqwik.api.lifecycles.*;

public class AutoCloseableLifecycle implements PropertyLifecycle {
	@Override
	public void doFinally(PropertyLifecycleContext propertyLifecycleContext) throws Throwable {
		if (propertyLifecycleContext.testInstance() instanceof AutoCloseable) {
			AutoCloseable closeable = (AutoCloseable) propertyLifecycleContext.testInstance();
			closeable.close();
		}
	}
}
