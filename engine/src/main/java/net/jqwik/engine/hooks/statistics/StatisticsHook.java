package net.jqwik.engine.hooks.statistics;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;
import net.jqwik.engine.hooks.*;

public class StatisticsHook implements AroundPropertyHook, PropagateToChildren {

	private static final Supplier<Map<String, StatisticsCollectorImpl>> STATISTICS_MAP_SUPPLIER =
		() -> new HashMap<String, StatisticsCollectorImpl>() {
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

	private void report(Map<String, StatisticsCollectorImpl> collectors, Reporter reporter, String propertyName) {
		for (StatisticsCollectorImpl collector : collectors.values()) {
			StatisticsPublisher reportGenerator = new StatisticsPublisher(collector, new StandardStatisticsReportFormat());
			reportGenerator.publish(reporter, propertyName);
		}
	}

	@Override
	public int aroundPropertyProximity() {
		return Hooks.AroundProperty.STATISTICS_PROXIMITY;
	}

}
