package net.jqwik.engine.hooks.statistics;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;

public class StatisticsHook implements AroundPropertyHook, PropagateToChildren {

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		StatisticsCollectorImpl.clearAll();
		PropertyExecutionResult testExecutionResult = property.execute();
		StatisticsCollectorImpl.report(context.reporter(), context.label());
		return testExecutionResult;
	}

	@Override
	public int aroundPropertyProximity() {
		// Should run inside AutoCloseableHook
		return -90;
	}

}
