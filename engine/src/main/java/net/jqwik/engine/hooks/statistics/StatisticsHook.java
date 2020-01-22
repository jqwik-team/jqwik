package net.jqwik.engine.hooks.statistics;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;

public class StatisticsHook implements AroundPropertyHook, PropagateToChildren {

	private static final Supplier<Map<String, StatisticsCollectorImpl>> STATISTICS_MAP_SUPPLIER = () ->
		new HashMap<String, StatisticsCollectorImpl>() {
			@Override
			public StatisticsCollectorImpl get(Object key) {
				return this.computeIfAbsent((String) key, StatisticsCollectorImpl::new);
			}
		};

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		Store<Map<String, StatisticsCollectorImpl>> collectorsStore =
			Store.create(
				Store.Visibility.LOCAL,
				StatisticsCollectorImpl.STORE_NAME,
				STATISTICS_MAP_SUPPLIER
			);
		PropertyExecutionResult testExecutionResult = property.execute();
		report(collectorsStore.get(), context.reporter(), context.extendedLabel());
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
