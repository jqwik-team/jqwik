package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.engine.reporting.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

import static net.jqwik.api.lifecycle.TryExecutionResult.Status.*;

public class GenericProperty {

	private final String name;
	private final PropertyConfiguration configuration;
	private final ShrinkablesGenerator shrinkablesGenerator;
	private final TryExecutor tryExecutor;

	public GenericProperty(
		String name,
		PropertyConfiguration configuration,
		ShrinkablesGenerator shrinkablesGenerator,
		TryExecutor tryExecutor
	) {
		this.name = name;
		this.configuration = configuration;
		this.shrinkablesGenerator = shrinkablesGenerator;
		this.tryExecutor = tryExecutor;
	}

	public PropertyCheckResult check(Consumer<ReportEntry> reporter, Reporting[] reporting) {
		int maxTries = configuration.getTries();
		int countChecks = 0;
		int countTries = 0;
		while (countTries < maxTries) {
			if (!shrinkablesGenerator.hasNext()) {
				break;
			} else {
				countTries++;
			}
			List<Shrinkable<Object>> shrinkableParams = shrinkablesGenerator.next();
			try {
				countChecks++;
				TryExecutionResult tryExecutionResult = testPredicate(shrinkableParams, reporter, reporting);
				if (tryExecutionResult.status() == FALSIFIED) {
					return shrinkAndCreateCheckResult(reporter, reporting, countChecks, countTries, shrinkableParams, tryExecutionResult
																														  .throwable());
				}
				if (tryExecutionResult.status() == INVALID) {
					countChecks--;
				}
			} catch (Throwable throwable) {
				// Only not AssertionErrors and non Exceptions get here
				JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
				return PropertyCheckResult.failed(
					configuration.getStereotype(), name, countTries, countChecks, configuration.getSeed(),
					configuration.getGenerationMode(), extractParams(shrinkableParams), null, throwable
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
		List<Shrinkable<Object>> shrinkableParams,
		Consumer<ReportEntry> reporter,
		Reporting[] reporting
	) {
		List<Object> plainParams = extractParams(shrinkableParams);
		if (Reporting.GENERATED.containedIn(reporting)) {
			reporter.accept(ReportEntry.from("generated", JqwikStringSupport.displayString(plainParams)));
		}
		return tryExecutor.execute(plainParams);
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
		int countTries, List<Shrinkable<Object>> shrinkables, Optional<Throwable> optionalThrowable
	) {
		List<Object> originalParams = extractParams(shrinkables);
		PropertyShrinkingResult shrinkingResult = shrink(reporter, reporting, shrinkables, optionalThrowable.orElse(null));
		List<Object> shrunkParams = shrinkingResult.values();
		Throwable throwable = shrinkingResult.throwable().orElse(null);
		return PropertyCheckResult.failed(
			configuration.getStereotype(), name, countTries, countChecks, configuration.getSeed(),
			configuration.getGenerationMode(), shrunkParams, originalParams, throwable
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
