package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.engine.reporting.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

public class GenericProperty {

	private final String name;
	private final PropertyConfiguration configuration;
	private final ParametersGenerator parametersGenerator;
	private final TryExecutor tryExecutor;

	public GenericProperty(
		String name,
		PropertyConfiguration configuration,
		ParametersGenerator parametersGenerator,
		TryExecutor tryExecutor
	) {
		this.name = name;
		this.configuration = configuration;
		this.parametersGenerator = parametersGenerator;
		this.tryExecutor = tryExecutor;
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

			List<Shrinkable<Object>> shrinkableParams = parametersGenerator.next();
			List<Object> sample = extractParams(shrinkableParams);

			try {
				countChecks++;
				TryExecutionResult tryExecutionResult = testPredicate(sample, reporter, reporting);
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
					configuration.getGenerationMode(), sample, null, throwable
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
				configuration.getGenerationMode()
			);
		}
		return PropertyCheckResult.successful(
			configuration.getStereotype(),
			name,
			countTries,
			countChecks,
			configuration.getSeed(),
			configuration.getGenerationMode()
		);
	}

	private TryExecutionResult testPredicate(
		List<Object> sample,
		Consumer<ReportEntry> reporter,
		Reporting[] reporting
	) {
		if (Reporting.GENERATED.containedIn(reporting)) {
			reporter.accept(ReportEntry.from("generated", JqwikStringSupport.displayString(sample)));
		}
		return tryExecutor.execute(sample);
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
			configuration.getStereotype(), name, countTries, countChecks, configuration.getSeed(),
			configuration.getGenerationMode(), shrunkParams, originalSample, throwable
		);
	}

	private PropertyShrinkingResult shrink(
		Consumer<ReportEntry> reporter,
		Reporting[] reporting,
		List<Shrinkable<Object>> shrinkables,
		Throwable exceptionOrAssertionError
	) {
		PropertyShrinker shrinker = new PropertyShrinker(shrinkables, configuration.getShrinkingMode(), reporter, reporting);
		Falsifier<List<Object>> forAllFalsifier = createFalsifier(tryExecutor);
		return shrinker.shrink(forAllFalsifier, exceptionOrAssertionError);
	}

	private Falsifier<List<Object>> createFalsifier(TryExecutor tryExecutor) {
		return tryExecutor::execute;
	}

}
