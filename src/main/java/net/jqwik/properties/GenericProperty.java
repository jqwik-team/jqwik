package net.jqwik.properties;

import static net.jqwik.properties.PropertyCheckResult.Status.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.util.*;
import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.support.*;

public class GenericProperty {

	private final String name;
	private final List<Arbitrary> arbitraries;
	private final Predicate<List<Object>> forAllPredicate;

	public GenericProperty(String name, List<Arbitrary> arbitraries, CheckedFunction forAllPredicate) {
		this.name = name;
		this.arbitraries = arbitraries;
		this.forAllPredicate = forAllPredicate;
	}

	public PropertyCheckResult check(PropertyConfiguration configuration, Consumer<ReportEntry> reporter) {
		StatisticsCollector.clearAll();
		PropertyCheckResult checkResult = checkWithoutReporting(configuration, reporter);
		reportResult(reporter, checkResult);
		reportStatistics(reporter);
		return checkResult;
	}

	private void reportStatistics(Consumer<ReportEntry> reporter) {
		StatisticsCollector.report(reporter);
	}

	private void reportResult(Consumer<ReportEntry> publisher, PropertyCheckResult checkResult) {
		if (checkResult.countTries() > 1 || checkResult.status() != SATISFIED)
			publisher.accept(CheckResultReportEntry.from(checkResult));
	}

	private PropertyCheckResult checkWithoutReporting(PropertyConfiguration configuration, Consumer<ReportEntry> reporter) {
		List<RandomGenerator> generators = arbitraries.stream().map(a1 -> a1.generator(configuration.getTries()))
				.collect(Collectors.toList());
		int maxTries = generators.isEmpty() ? 1 : configuration.getTries();
		int countChecks = 0;
		Random random = new Random(configuration.getSeed());
		for (int countTries = 1; countTries <= maxTries; countTries++) {
			List<Shrinkable> shrinkableParams = generateParameters(generators, random);
			try {
				countChecks++;
				if (!testPredicate(shrinkableParams, configuration.getReportingMode(), reporter)) {
					return shrinkAndCreateCheckResult(configuration.getStereotype(), configuration.getShrinkingMode(),
							configuration.getSeed(), countChecks, countTries, shrinkableParams, null);
				}
			} catch (AssertionError ae) {
				return shrinkAndCreateCheckResult(configuration.getStereotype(), configuration.getShrinkingMode(), configuration.getSeed(),
						countChecks, countTries, shrinkableParams, ae);
			} catch (TestAbortedException tae) {
				countChecks--;
				continue;
			} catch (Throwable throwable) {
				BlacklistedExceptions.rethrowIfBlacklisted(throwable);
				return PropertyCheckResult.erroneous(configuration.getStereotype(), name, countTries, countChecks, configuration.getSeed(),
						extractParams(shrinkableParams), throwable);
			}
		}
		if (countChecks == 0 || maxDiscardRatioExceeded(countChecks, maxTries, configuration.getMaxDiscardRatio()))
			return PropertyCheckResult.exhausted(configuration.getStereotype(), name, maxTries, countChecks, configuration.getSeed());
		return PropertyCheckResult.satisfied(configuration.getStereotype(), name, maxTries, countChecks, configuration.getSeed());
	}

	private boolean testPredicate(List<Shrinkable> shrinkableParams, ReportingMode reportingMode, Consumer<ReportEntry> reporter) {
		List<Object> plainParams = extractParams(shrinkableParams);
		if (reportingMode == ReportingMode.GENERATED) {
			reporter.accept(ReportEntry.from("generated", JqwikStringSupport.displayString(plainParams)));
		}
		return forAllPredicate.test(plainParams);
	}

	private boolean maxDiscardRatioExceeded(int countChecks, int countTries, int maxDiscardRatio) {
		int actualDiscardRatio = (countTries - countChecks) / countChecks;
		return actualDiscardRatio > maxDiscardRatio;
	}

	private List<Object> extractParams(List<Shrinkable> shrinkableParams) {
		return shrinkableParams.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private PropertyCheckResult shrinkAndCreateCheckResult(String stereotype, ShrinkingMode shrinkingMode, long seed, int countChecks,
			int countTries, List<Shrinkable> shrinkables, AssertionError error) {
		List<Object> originalParams = extractParams(shrinkables);
		if (shrinkingMode == ShrinkingMode.OFF) {
			return PropertyCheckResult.falsified(stereotype, name, countTries, countChecks, seed, originalParams, originalParams, error);
		}
		ParameterListShrinker shrinker = new ParameterListShrinker(shrinkables);
		ShrinkResult<List<Shrinkable>> shrinkResult = shrinker.shrink(forAllPredicate, error);
		List<Object> shrunkParams = extractParams(shrinkResult.shrunkValue());
		Throwable throwable = shrinkResult.throwable().orElse(null);
		return PropertyCheckResult.falsified(stereotype, name, countTries, countChecks, seed, shrunkParams, originalParams, throwable);
	}

	private List<Shrinkable> generateParameters(List<RandomGenerator> generators, Random random) {
		return generators.stream().map(generator -> generator.next(random)).collect(Collectors.toList());
	}
}
