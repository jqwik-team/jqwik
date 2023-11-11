package net.jqwik.engine.hooks.lifecycle;

import org.junit.platform.engine.support.hierarchical.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.hooks.*;
import net.jqwik.engine.support.*;

public class AutoCloseableHook implements AroundPropertyHook {

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		PropertyExecutionResult testExecutionResult = property.execute();
		executeCloseMethods(context);
		return testExecutionResult;
	}

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}

	@Override
	public int aroundPropertyProximity() {
		return Hooks.AroundProperty.AUTO_CLOSEABLE_PROXIMITY;
	}

	private void executeCloseMethods(PropertyLifecycleContext context) {
		ThrowableCollector throwableCollector = new ThrowableCollector(ignore -> false);

		context.testInstances().forEach(innerInstance -> {
			if (innerInstance instanceof AutoCloseable) {
				throwableCollector.execute(((AutoCloseable) innerInstance)::close);
			}
		});
		throwableCollector.assertEmpty();
	}
}
