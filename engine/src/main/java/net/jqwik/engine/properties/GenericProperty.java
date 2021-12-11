package net.jqwik.engine.properties;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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

	public PropertyCheckResult check(Reporter reporter, Reporting[] reporting) {
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

			List<Shrinkable<Object>> shrinkableParams;
			TryLifecycleContext tryLifecycleContext;
			try {
				Tuple.Tuple2<TryLifecycleContext, List<Shrinkable<Object>>> pair = parametersGenerator.next(tryLifecycleContextSupplier);
				tryLifecycleContext = pair.get1();
				shrinkableParams = pair.get2();
			} catch (Throwable throwable) {
				// Mostly TooManyFilterMissesException gets here
				JqwikExceptionSupport.rethrowIfBlacklisted(throwable);

				return exhaustedCheckResult(countTries, countChecks, throwable);
			}

			List<Object> sample = extractParams(shrinkableParams);
			try {
				countChecks++;
				TryExecutionResult tryExecutionResult = testPredicate(tryLifecycleContext, sample, reporter, reporting);
				switch (tryExecutionResult.status()) {
					case SATISFIED:
						finishEarly = tryExecutionResult.shouldPropertyFinishEarly();
						continue;
					case FALSIFIED:
						FalsifiedSample falsifiedSample = new FalsifiedSampleImpl(
							sample,
							shrinkableParams,
							tryExecutionResult.throwable(),
							tryExecutionResult.footnotes()
						);
						return shrinkAndCreateCheckResult(
							reporter,
							reporting,
							countChecks,
							countTries,
							falsifiedSample,
							tryLifecycleContext.targetMethod()
						);
					case INVALID:
						countChecks--;
						if (maxTries == 1) { // Examples have exactly one try
							return PropertyCheckResult.skipExample(
								configuration.getStereotype(),
								name,
								configuration.getSeed(),
								configuration.getGenerationMode(),
								configuration.getEdgeCasesMode(),
								parametersGenerator.edgeCasesTotal(),
								parametersGenerator.edgeCasesTried(),
								tryExecutionResult.throwable().orElse(null)
							);
						}
						break;
					default:
						String message = String.format("Unknown TryExecutionResult.status [%s]", tryExecutionResult.status().name());
						throw new RuntimeException(message);
				}
			} catch (Throwable throwable) {
				// Only not AssertionErrors and non Exceptions get here
				JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
				FalsifiedSample falsifiedSample = new FalsifiedSampleImpl(
					sample,
					shrinkableParams,
					Optional.of(throwable),
					Collections.emptyList()
				);
				GenerationInfo generationInfo = new GenerationInfo(configuration.getSeed(), parametersGenerator.generationIndex());
				return PropertyCheckResult.failed(
					configuration.getStereotype(), name, countTries, countChecks, generationInfo,
					configuration.getGenerationMode(),
					configuration.getEdgeCasesMode(), parametersGenerator.edgeCasesTotal(), parametersGenerator.edgeCasesTried(),
					falsifiedSample, null, throwable
				);
			}
		}
		if (countChecks == 0 || maxDiscardRatioExceeded(countChecks, countTries, configuration.getMaxDiscardRatio())) {
			return exhaustedCheckResult(maxTries, countChecks, null);
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

	private PropertyCheckResult exhaustedCheckResult(int countTries, int countChecks, Throwable throwable) {
		return PropertyCheckResult.exhausted(
			configuration.getStereotype(),
			name,
			countTries,
			countChecks,
			configuration.getSeed(),
			configuration.getGenerationMode(),
			configuration.getEdgeCasesMode(),
			parametersGenerator.edgeCasesTotal(),
			parametersGenerator.edgeCasesTried(),
			throwable
		);
	}

	private TryExecutionResult testPredicate(
		TryLifecycleContext tryLifecycleContext,
		List<Object> sample,
		Reporter reporter,
		Reporting[] reporting
	) {
		if (Reporting.GENERATED.containedIn(reporting)) {
			Map<String, Object> reports = SampleReporter.createSampleReports(tryLifecycleContext.targetMethod(), sample);
			reporter.publishReports("generated", reports);
		}
		return tryLifecycleExecutor.execute(tryLifecycleContext, sample);
	}

	private boolean maxDiscardRatioExceeded(int countChecks, int countTries, int maxDiscardRatio) {
		int actualDiscardRatio = (countTries - countChecks) / countChecks;
		return actualDiscardRatio > maxDiscardRatio;
	}

	private List<Object> extractParams(List<Shrinkable<Object>> shrinkableParams) {
		return shrinkableParams.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	private PropertyCheckResult shrinkAndCreateCheckResult(
		Reporter reporter, Reporting[] reporting, int countChecks,
		int countTries, FalsifiedSample originalSample,
		Method targetMethod
	) {
		ShrunkFalsifiedSample shrunkSample = shrink(reporter, reporting, originalSample, targetMethod);
		GenerationInfo generationInfo = new GenerationInfo(configuration.getSeed(), parametersGenerator.generationIndex());
		return PropertyCheckResult.failed(
			configuration.getStereotype(), name, countTries, countChecks, generationInfo, configuration.getGenerationMode(),
			configuration.getEdgeCasesMode(), parametersGenerator.edgeCasesTotal(), parametersGenerator.edgeCasesTried(),
			originalSample, shrunkSample, shrunkSample.falsifyingError().orElse(null)
		);
	}

	private ShrunkFalsifiedSample shrink(
		Reporter reporter,
		Reporting[] reporting,
		FalsifiedSample originalSample,
		Method targetMethod
	) {
		// TODO: Find a way that falsifier and resolved ParameterSupplier get the same instance of tryLifecycleContext during shrinking.
		//       This will probably require some major modification to shrinking / shrinking API.
		//       Maybe introduce some decorator for ShrinkingSequence(s)

		Consumer<FalsifiedSample> falsifiedSampleReporter = createFalsifiedSampleReporter(reporter, reporting);
		PropertyShrinker shrinker = new PropertyShrinker(
			originalSample,
			configuration.getShrinkingMode(),
			configuration.boundedShrinkingSeconds(),
			falsifiedSampleReporter,
			targetMethod
		);

		Falsifier<List<Object>> forAllFalsifier = createFalsifier(tryLifecycleContextSupplier, tryLifecycleExecutor);
		return shrinker.shrink(forAllFalsifier);
	}

	private Consumer<FalsifiedSample> createFalsifiedSampleReporter(Reporter reporter, Reporting[] reporting) {
		return sample -> {
			if (Reporting.FALSIFIED.containedIn(reporting)) {
				TryLifecycleContext tryLifecycleContext = tryLifecycleContextSupplier.get();
				Map<String, Object> reports = SampleReporter.createSampleReports(tryLifecycleContext.targetMethod(), sample.parameters());
				reporter.publishReports("falsified", reports);
			}
		};
	}

	private Falsifier<List<Object>> createFalsifier(Supplier<TryLifecycleContext> tryLifecycleContext, TryLifecycleExecutor tryExecutor) {
		return params -> tryExecutor.execute(tryLifecycleContext.get(), params);
	}

}
