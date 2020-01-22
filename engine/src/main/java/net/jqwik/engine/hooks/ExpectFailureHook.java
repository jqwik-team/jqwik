package net.jqwik.engine.hooks;

import net.jqwik.api.lifecycle.*;

public class ExpectFailureHook implements AroundPropertyHook {

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		PropertyExecutionResult testExecutionResult = property.execute();
		if (testExecutionResult.getStatus() == PropertyExecutionResult.Status.FAILED) {
			return testExecutionResult.changeToSuccessful();
		}
		String message = String.format("Property [%s] should have failed but did not", context.label());
		return testExecutionResult.changeToFailed(message);
	}

	@Override
	public int aroundPropertyProximity() {
		// Should run inside StatisticsHook
		return -50;
	}

}
