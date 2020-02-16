package net.jqwik.api.lifecycle.hooks;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.PerProperty.*;

public class PerPropertyHook implements AroundPropertyHook {
	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		Optional<PerProperty> perProperty = context.findAnnotation(PerProperty.class);
		Class<? extends PerPropertyLifecycle> lifecycleClass = perProperty.map(pp -> pp.value()).orElseThrow(() -> {
			String message = "@PerProperty annotation MUST have a value() attribute";
			return new JqwikException(message);
		});
		PerPropertyLifecycle lifecycle = context.newInstance(lifecycleClass);

		lifecycle.before(context);

		PropertyExecutionResult executionResult = property.execute();
		try {
			if (executionResult.status() == PropertyExecutionResult.Status.SUCCESSFUL) {
				try {
					lifecycle.onSuccess();
				} catch (Throwable throwable) {
					return executionResult.mapToFailed(throwable);
				}
			} else if (executionResult.status() == PropertyExecutionResult.Status.FAILED) {
				return lifecycle.onFailure(executionResult);
			}
			return executionResult;
		} finally {
			lifecycle.after();
		}
	}

	@Override
	public int aroundPropertyProximity() {
		// Somewhat closer than standard hooks
		return 5;
	}
}
