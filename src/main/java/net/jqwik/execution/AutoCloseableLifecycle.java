package net.jqwik.execution;

import net.jqwik.api.lifecycles.*;
import net.jqwik.support.*;

import java.util.*;

public class AutoCloseableLifecycle implements PropertyFinallyLifecycle {
	@Override
	public void finallyAfterProperty(PropertyLifecycleContext propertyLifecycleContext) throws Throwable {
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
