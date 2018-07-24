package net.jqwik.execution;

import net.jqwik.api.lifecycle.*;
import net.jqwik.support.*;
import org.junit.platform.engine.*;
import org.opentest4j.*;

import java.util.*;

public class AutoCloseableHook implements AroundPropertyHook {

	@Override
	public TestExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		TestExecutionResult testExecutionResult = property.execute();
		List<Throwable> throwableCollector = executeCloseMethods(context);
		if (!throwableCollector.isEmpty()) {
			handleExceptions(throwableCollector);
		}
		return testExecutionResult;
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
