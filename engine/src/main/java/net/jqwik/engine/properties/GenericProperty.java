package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.engine.reporting.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.execution.reporting.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

public class GenericProperty {

	private final String name;
	private final PropertyConfiguration configuration;
	private final ParametersGenerator parametersGenerator;
	private final TryLifecycleExecutor tryLifecycleExecutor;
	private final Supplier<TryLifecycleContext> tryLifecycleContextSupplier;

	public GenericProperty(
		String name,
		PropertyConfiguration configuration,
		ParametersGenerator parametersGenerator,
		TryLifecycleExecutor tryLifecycleExecutor,
		Supplier<TryLifecycleContext> tryLifecycleContextSupplier
	) {
		this.name = name;
		this.configuration = configuration;
		this.parametersGenerator = parametersGenerator;
		this.tryLifecycleExecutor = tryLifecycleExecutor;
		this.tryLifecycleContextSupplier = tryLifecycleContextSupplier;
	}

	public PropertyCheckResult check(Consumer<ReportEntry> reporter, Reporting[] reporting) {
		int maxTries = configuration.getTries();
		int countChecks = 0;
		int countTries = 0;
		boolean finishEarly = false;
		while (countTries < maxTries) {
			if (finishEarly) {
				break;
			}
			if (!parametersGenerator.hasNext()) {
				break;
			}
			countTries++;

			TryLifecycleContext tryLifecycleContext = tryLifecycleContextSupplier.get();
			List<Shrinkable<Object>> shrinkableParams = parametersGenerator.next(tryLifecycleContext);
			List<Object> sample = extractParams(shrinkableParams);

			try {
				countChecks++;
				TryExecutionResult tryExecutionResult = testPredicate(tryLifecycleContext, sample, reporter, reporting);
				switch (tryExecutionResult.status()) {
					case SATISFIED:
						finishEarly = tryExecutionResult.shouldPropertyFinishEarly();
						continue;
					case FALSIFIED:
						return shrinkAndCreateCheckResult(
							reporter,
							reporting,
							countChecks,
							countTries,
							shrinkableParams,
							sample,
							tryExecutionResult.throwable()
						);
					case INVALID:
						countChecks--;
						break;
					default:
						String message = String.format("Unknown TryExecutionResult.status [%s]", tryExecutionResult.status().name());
						throw new RuntimeException(message);
				}
			} catch (Throwable throwable) {
				// Only not AssertionErrors and non Exceptions get here
				JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
				return PropertyCheckResult.failed(
					configuration.getStereotype(), name, countTries, countChecks, configuration.getSeed(),
					configuration.getGenerationMode(),
					configuration.getEdgeCasesMode(), parametersGenerator.edgeCasesTotal(), parametersGenerator.edgeCasesTried(),
					sample, null, throwable
				);
			}
		}
		if (countChecks == 0 || maxDiscardRatioExceeded(countChecks, countTries, configuration.getMaxDiscardRatio())) {
			return PropertyCheckResult.exhausted(
				configuration.getStereotype(),
				name,
				maxTries,
				countChecks,
				configuration.getSeed(),
				configuration.getGenerationMode(),
				configuration.getEdgeCasesMode(),
				parametersGenerator.edgeCasesTotal(),
				parametersGenerator.edgeCasesTried()
			);
		}
		return PropertyCheckResult.successful(
			configuration.getStereotype(),
			name,
			countTries,
			countChecks,
			configuration.getSeed(),
			configuration.getGenerationMode(),
			configuration.getEdgeCasesMode(),
			parametersGenerator.edgeCasesTotal(),
			parametersGenerator.edgeCasesTried()
		);
	}

	private TryExecutionResult testPredicate(
		TryLifecycleContext tryLifecycleContext,
		List<Object> sample,
		Consumer<ReportEntry> reporter,
		Reporting[] reporting
	) {
		if (Reporting.GENERATED.containedIn(reporting)) {
			String sampleReport = sampleReport(tryLifecycleContext.targetMethod(), sample);
			reporter.accept(ReportEntry.from("generated", sampleReport));
		}
		return tryLifecycleExecutor.execute(tryLifecycleContext, sample);
	}

	private String sampleReport(Method method, List<Object> sample) {
		StringBuilder stringBuilder = new StringBuilder();
		SampleReporter.reportSample(stringBuilder, method, sample, null);
		removeTrailingNewLine(stringBuilder);
		return stringBuilder.toString();
	}

	private void removeTrailingNewLine(final StringBuilder stringBuilder) {
		int lastNewLine = stringBuilder.lastIndexOf(String.format("%n"));
		if (lastNewLine + 1 == stringBuilder.length()) {
			stringBuilder.replace(lastNewLine, lastNewLine + 1, "");
		}
	}

	private boolean maxDiscardRatioExceeded(int countChecks, int countTries, int maxDiscardRatio) {
		int actualDiscardRatio = (countTries - countChecks) / countChecks;
		return actualDiscardRatio > maxDiscardRatio;
	}

	private List<Object> extractParams(List<Shrinkable<Object>> shrinkableParams) {
		return shrinkableParams.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	private PropertyCheckResult shrinkAndCreateCheckResult(
		Consumer<ReportEntry> reporter, Reporting[] reporting, int countChecks,
		int countTries, List<Shrinkable<Object>> shrinkables, List<Object> originalSample, Optional<Throwable> optionalThrowable
	) {
		PropertyShrinkingResult shrinkingResult = shrink(reporter, reporting, shrinkables, optionalThrowable.orElse(null));
		List<Object> shrunkParams = shrinkingResult.values();
		Throwable throwable = shrinkingResult.throwable().orElse(null);
		return PropertyCheckResult.failed(
			configuration.getStereotype(), name, countTries, countChecks, configuration.getSeed(), configuration.getGenerationMode(),
			configuration.getEdgeCasesMode(), parametersGenerator.edgeCasesTotal(), parametersGenerator.edgeCasesTried(),
			shrunkParams, originalSample, throwable
		);
	}

	private PropertyShrinkingResult shrink(
		Consumer<ReportEntry> reporter,
		Reporting[] reporting,
		List<Shrinkable<Object>> shrinkables,
		Throwable exceptionOrAssertionError
	) {
		// TODO: Find a way that falsifier and resolved ParameterSupplier get the same instance of tryLifecycleContext during shrinking.
		//       This will probably require some major modification to shrinking / shrinking API.
		//       Maybe introduce some decorator for ShrinkingSequence(s)
		Function<List<Object>, String> sampleReportCreator = sample -> {
			TryLifecycleContext tryLifecycleContext = tryLifecycleContextSupplier.get();
			String sampleReport = sampleReport(tryLifecycleContext.targetMethod(), sample);
			return sampleReport;
		};
		PropertyShrinker shrinker = new PropertyShrinker(shrinkables, configuration.getShrinkingMode(), reporter, reporting, sampleReportCreator);
		Falsifier<List<Object>> forAllFalsifier = createFalsifier(tryLifecycleContextSupplier, tryLifecycleExecutor);
		return shrinker.shrink(forAllFalsifier, exceptionOrAssertionError);
	}

	private Falsifier<List<Object>> createFalsifier(
		Supplier<TryLifecycleContext> tryLifecycleContext,
		TryLifecycleExecutor tryExecutor
	) {
		return params -> tryExecutor.execute(tryLifecycleContext.get(), params);
	}

}
