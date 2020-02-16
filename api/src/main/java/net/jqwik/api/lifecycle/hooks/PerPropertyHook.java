package net.jqwik.api.lifecycle.hooks;

import net.jqwik.api.lifecycle.*;

public class PerPropertyHook implements AroundPropertyHook {
	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
		return property.execute();
	}
}
