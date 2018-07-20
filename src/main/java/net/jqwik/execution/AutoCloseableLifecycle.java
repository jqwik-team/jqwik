package net.jqwik.execution;

import net.jqwik.api.lifecycle.*;
import net.jqwik.support.*;

import java.util.*;

public class AutoCloseableLifecycle implements TeardownPropertyHook {
	@Override
	public void teardownProperty(PropertyLifecycleContext propertyLifecycleContext) throws Throwable {
		List<Throwable> throwableCollector = new ArrayList<>();
		JqwikReflectionSupport.streamInstancesFromInside(propertyLifecycleContext.testInstance()).forEach(innerInstance -> {
			if (innerInstance instanceof AutoCloseable) {
				try {
					((AutoCloseable) innerInstance).close();
				} catch (Throwable t) {
					throwableCollector.add(t);
				}
			}
		});

		if (!throwableCollector.isEmpty()) {
			// TODO: Use MultiException for reporting all exceptions
			throw throwableCollector.get(0);
		}
	}
}
