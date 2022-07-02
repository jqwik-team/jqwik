package net.jqwik.engine.hooks.statistics;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.statistics.*;
import net.jqwik.api.statistics.StatisticsReport.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.hooks.*;
import net.jqwik.engine.support.*;

public class StatisticsHook implements AroundPropertyHook {

	private static final Logger LOG = Logger.getLogger(StatisticsHook.class.getName());

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
		if (testExecutionResult.status() == PropertyExecutionResult.Status.SUCCESSFUL) {
			testExecutionResult = checkCoverages(testExecutionResult, collectors.values());
		}

		createStatisticsReports(collectors, context, testExecutionResult);

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

	private void createStatisticsReports(
		Map<String, StatisticsCollectorImpl> collectors,
		PropertyLifecycleContext context,
		PropertyExecutionResult testExecutionResult
	) {
		List<StatisticsReport> statisticsReportAnnotations = JqwikAnnotationSupport.findRepeatableAnnotationOnElementOrContainer(
			context.targetMethod(),
			StatisticsReport.class
		);

		boolean isFailure = testExecutionResult.status() == PropertyExecutionResult.Status.FAILED;
		Set<Tuple3<String, StatisticsCollectorImpl, StatisticsReportFormat>> reports =
			collectors.entrySet().stream()
					  .map(entry -> {
						  String label = entry.getKey();
						  return Tuple.of(label, entry.getValue(), determineFormat(label, statisticsReportAnnotations, context, isFailure));
					  })
					  .collect(CollectorsSupport.toLinkedHashSet());

		report(reports, context.reporter(), context.extendedLabel());
	}

	private StatisticsReportFormat determineFormat(
		String label,
		List<StatisticsReport> statisticsReportAnnotations,
		PropertyLifecycleContext context,
		boolean isFailure
	) {
		boolean defaultReportFormatSet = false;
		StatisticsReportFormat defaultReportFormat = new StandardStatisticsReportFormat();

		for (StatisticsReport annotation : statisticsReportAnnotations) {
			if (annotation.label().equals(label)) {
				return createReportFormat(annotation, context, isFailure);
			}
			if (!defaultReportFormatSet && annotation.label().equals(StatisticsReport.ALL_LABELS)) {
				defaultReportFormat = createReportFormat(annotation, context, isFailure);
				defaultReportFormatSet = true;
			}
		}
		return defaultReportFormat;
	}

	private StatisticsReportFormat createReportFormat(StatisticsReport annotation, PropertyLifecycleContext context, boolean isFailure) {
		if (annotation.value() == StatisticsReportMode.OFF) {
			if (annotation.onFailureOnly()) {
				String message = String.format(
					"@StatisticsReport(onFailureOnly = true, value = StatisticsReport.StatisticsReportMode.OFF) " +
						"does not make sense.%n" +
						"You should change 'onFailureOnly' or 'value'."
				);
				LOG.warning(message);
			}
			return null;
		} else if (annotation.onFailureOnly() && !isFailure) {
			return null;
		} else if (annotation.value() == StatisticsReportMode.PLUG_IN && !Objects.equals(annotation.format(), None.class)) {
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
