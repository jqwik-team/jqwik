package net.jqwik.engine.hooks.statistics;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.statistics.*;
import net.jqwik.api.statistics.StatisticsReport.*;
import net.jqwik.engine.hooks.*;
import net.jqwik.engine.support.*;

public class StatisticsHook implements AroundPropertyHook {

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
				StatisticsCollectorImpl.COLLECTORS_ID,
				Store.Lifespan.PROPERTY,
				STATISTICS_MAP_SUPPLIER
			);

		PropertyExecutionResult testExecutionResult = property.execute();

		afterExecution(collectorsStore.get(), context);
		return testExecutionResult;
	}

	private void afterExecution(Map<String, StatisticsCollectorImpl> collectors, PropertyLifecycleContext context) {
		StatisticsReportFormat reportFormat = new StandardStatisticsReportFormat();
		Optional<StatisticsReport> optionalStatisticsReport =
			JqwikAnnotationSupport.findAnnotationOnElementOrContainer(context.targetMethod(), StatisticsReport.class);

		if (optionalStatisticsReport.isPresent()) {
			StatisticsReport reportConfiguration = optionalStatisticsReport.get();
			if (reportConfiguration.value() == StatisticsReportMode.OFF) {
				return;
			}
			if (reportConfiguration.value() == StatisticsReportMode.PLUG_IN) {
				reportFormat = JqwikReflectionSupport.newInstanceInTestContext(reportConfiguration.format(), context.testInstance());
			}
		}
		report(collectors, context.reporter(), context.extendedLabel(), reportFormat);
	}

	private void report(
		Map<String, StatisticsCollectorImpl> collectors,
		Reporter reporter,
		String propertyName,
		StatisticsReportFormat reportFormat
	) {
		for (StatisticsCollectorImpl collector : collectors.values()) {
			StatisticsPublisher reportGenerator = new StatisticsPublisher(collector, reportFormat);
			reportGenerator.publish(reporter, propertyName);
		}
	}

	@Override
	public boolean applyToDescendants() {
		return true;
	}

	@Override
	public int aroundPropertyProximity() {
		return Hooks.AroundProperty.STATISTICS_PROXIMITY;
	}

}
