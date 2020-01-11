package net.jqwik.engine.hooks.statistics;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;

public class StatisticsHook implements AroundPropertyHook, PropagateToChildren {

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		Map<String, StatisticsCollectorImpl> collectors = new HashMap<>();
		StatisticsCollectorImpl.setCurrent(collectors);
		PropertyExecutionResult testExecutionResult = property.execute();
		report(collectors, context.reporter(), context.label());
		StatisticsCollectorImpl.setCurrent(null);
		return testExecutionResult;
	}

	private void report(
		Map<String, StatisticsCollectorImpl> collectors,
		Reporter reporter,
		String propertyName
	) {
		for (StatisticsCollectorImpl collector : collectors.values()) {
			Tuple.Tuple2<String, String> reportEntry = collector.createReportEntry(propertyName);
			reporter.publish(reportEntry.get1(), reportEntry.get2());
		}
	}

	@Override
	public int aroundPropertyProximity() {
		// Should run inside AutoCloseableHook
		return -90;
	}

}
