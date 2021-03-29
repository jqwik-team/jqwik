package net.jqwik.engine.hooks.statistics;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.statistics.*;
import net.jqwik.api.statistics.StatisticsReport.*;
import net.jqwik.engine.hooks.*;
import net.jqwik.engine.support.*;

public class StatisticsHook implements AroundPropertyHook {

	private static final Supplier<Map<String, StatisticsCollectorImpl>> STATISTICS_MAP_SUPPLIER =
		() -> new LinkedHashMap<String, StatisticsCollectorImpl>() {
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
				Lifespan.PROPERTY,
				STATISTICS_MAP_SUPPLIER
			);

		PropertyExecutionResult testExecutionResult = property.execute();

		Map<String, StatisticsCollectorImpl> collectors = collectorsStore.get();
		createStatisticsReports(collectors, context);
		if (testExecutionResult.status() == PropertyExecutionResult.Status.SUCCESSFUL) {
			return checkCoverages(testExecutionResult, collectors.values());
		}
		return testExecutionResult;
	}

	private PropertyExecutionResult checkCoverages(
		PropertyExecutionResult testExecutionResult,
		Collection<StatisticsCollectorImpl> collectors
	) {
		try {
			for (StatisticsCollectorImpl collector : collectors) {
				collector.checkCoverage();
			}
			return testExecutionResult;
		} catch (Throwable throwable) {
			JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
			return testExecutionResult.mapToFailed(throwable);
		}
	}

	private void createStatisticsReports(Map<String, StatisticsCollectorImpl> collectors, PropertyLifecycleContext context) {
		List<StatisticsReport> statisticsReportAnnotations = JqwikAnnotationSupport.findRepeatableAnnotationOnElementOrContainer(
			context.targetMethod(),
			StatisticsReport.class
		);

		Set<Tuple3<String, StatisticsCollectorImpl, StatisticsReportFormat>> reports =
			collectors.entrySet().stream()
					  .map(entry -> {
						  String label = entry.getKey();
						  return Tuple.of(label, entry.getValue(), determineFormat(label, statisticsReportAnnotations, context));
					  })
					  .collect(Collectors.toSet());

		report(reports, context.reporter(), context.extendedLabel());
	}

	private StatisticsReportFormat determineFormat(
		String label,
		List<StatisticsReport> statisticsReportAnnotations,
		PropertyLifecycleContext context
	) {
		boolean defaultReportFormatSet = false;
		StatisticsReportFormat defaultReportFormat = new StandardStatisticsReportFormat();

		for (StatisticsReport annotation : statisticsReportAnnotations) {
			if (annotation.label().equals(label)) {
				return createReportFormat(annotation, context);
			}
			if (!defaultReportFormatSet && annotation.label().equals(StatisticsReport.ALL_LABELS)) {
				defaultReportFormat = createReportFormat(annotation, context);
				defaultReportFormatSet = true;
			}
		}
		return defaultReportFormat;
	}

	private StatisticsReportFormat createReportFormat(StatisticsReport annotation, PropertyLifecycleContext context) {
		if (annotation.value() == StatisticsReportMode.OFF) {
			return null;
		} else if (annotation.value() == StatisticsReportMode.PLUG_IN) {
			return JqwikReflectionSupport.newInstanceInTestContext(annotation.format(), context.testInstance());
		} else {
			return new StandardStatisticsReportFormat();
		}
	}

	private void report(
		Set<Tuple3<String, StatisticsCollectorImpl, StatisticsReportFormat>> reports,
		Reporter reporter,
		String propertyName
	) {
		for (Tuple3<String, StatisticsCollectorImpl, StatisticsReportFormat> report : reports) {
			if (report.get3() == null) {
				continue;
			}
			StatisticsPublisher reportGenerator = new StatisticsPublisher(report.get2(), report.get3());
			reportGenerator.publish(reporter, propertyName);
		}
	}

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}

	@Override
	public int aroundPropertyProximity() {
		return Hooks.AroundProperty.STATISTICS_PROXIMITY;
	}

}
