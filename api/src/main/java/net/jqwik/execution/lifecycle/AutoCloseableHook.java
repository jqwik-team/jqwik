package net.jqwik.execution.lifecycle;

import java.util.*;

import org.opentest4j.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.execution.*;
import net.jqwik.support.*;

public class AutoCloseableHook implements AroundPropertyHook {

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		PropertyExecutionResult testExecutionResult = property.execute();
		List<Throwable> throwableCollector = executeCloseMethods(context);
		if (!throwableCollector.isEmpty()) {
			handleExceptions(throwableCollector);
		}
		return testExecutionResult;
	}

	@Override
	public int aroundPropertyProximity() {
		// AutoCloseable.close() should be the last thing in the hook chain
		// unless there is a hook with a proximity < -100
		return -100;
	}

	private List<Throwable> executeCloseMethods(PropertyLifecycleContext context) {
		List<Throwable> throwableCollector = new ArrayList<>();
		JqwikReflectionSupport.streamInstancesFromInside(context.testInstance()).forEach(innerInstance -> {
			if (innerInstance instanceof AutoCloseable) {
				try {
					((AutoCloseable) innerInstance).close();
				} catch (Exception t) {
					throwableCollector.add(t);
				}
			}
		});
		return throwableCollector;
	}

	private void handleExceptions(List<Throwable> throwableCollector) throws Throwable {
		Throwable throwable = throwableCollector.size() == 1 ?
			throwableCollector.get(0) :
			new MultipleFailuresError("Exceptions occurred during close()", throwableCollector);
		throw throwable;
	}
}
