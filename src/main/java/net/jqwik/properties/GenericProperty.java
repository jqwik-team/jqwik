package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.properties.shrinking.*;
import net.jqwik.support.*;
import org.junit.platform.commons.util.*;
import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static net.jqwik.properties.PropertyCheckResult.Status.*;

public class GenericProperty {

	private final String name;
	private final PropertyConfiguration configuration;
	private final ShrinkablesGenerator shrinkablesGenerator;
	private final CheckedFunction checkedFunction;

	public GenericProperty(
		String name,
		PropertyConfiguration configuration,
		ShrinkablesGenerator shrinkablesGenerator,
		CheckedFunction checkedFunction
	) {
		this.name = name;
		this.configuration = configuration;
		this.shrinkablesGenerator = shrinkablesGenerator;
		this.checkedFunction = checkedFunction;
	}

	public PropertyCheckResult check(Consumer<ReportEntry> reporter) {
		StatisticsCollector.clearAll();
		PropertyCheckResult checkResult = checkWithoutReporting(reporter);
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

	private PropertyCheckResult checkWithoutReporting(Consumer<ReportEntry> reporter) {
		int maxTries = configuration.getTries();
		int countChecks = 0;
		int countTries = 0;
		while (countTries < maxTries) {
			if (!shrinkablesGenerator.hasNext()) {
				break;
			} else {
				countTries++;
			}
			List<Shrinkable> shrinkableParams = shrinkablesGenerator.next();
			try {
				countChecks++;
				if (!testPredicate(shrinkableParams, configuration.getReporting(), reporter)) {
					return shrinkAndCreateCheckResult(reporter, countChecks, countTries, shrinkableParams, null);
				}
			} catch (AssertionError ae) {
				return shrinkAndCreateCheckResult(reporter, countChecks, countTries, shrinkableParams, ae);
			} catch (TestAbortedException tae) {
				countChecks--;
				continue;
			} catch (Throwable throwable) {
				BlacklistedExceptions.rethrowIfBlacklisted(throwable);
				return PropertyCheckResult.erroneous(configuration.getStereotype(), name, countTries, countChecks, configuration.getSeed(),
						extractParams(shrinkableParams), throwable);
			}
		}
		if (countChecks == 0 || maxDiscardRatioExceeded(countChecks, countTries, configuration.getMaxDiscardRatio()))
			return PropertyCheckResult.exhausted(configuration.getStereotype(), name, maxTries, countChecks, configuration.getSeed());
		return PropertyCheckResult.satisfied(configuration.getStereotype(), name, countTries, countChecks, configuration.getSeed());
	}

	private boolean testPredicate(List<Shrinkable> shrinkableParams, Reporting[] reporting, Consumer<ReportEntry> reporter) {
		List<Object> plainParams = extractParams(shrinkableParams);
		if (Reporting.GENERATED.containedIn(reporting)) {
			reporter.accept(ReportEntry.from("generated", JqwikStringSupport.displayString(plainParams)));
		}
		return checkedFunction.test(plainParams);
	}

	private boolean maxDiscardRatioExceeded(int countChecks, int countTries, int maxDiscardRatio) {
		int actualDiscardRatio = (countTries - countChecks) / countChecks;
		return actualDiscardRatio > maxDiscardRatio;
	}

	private List<Object> extractParams(List<Shrinkable> shrinkableParams) {
		return shrinkableParams.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	private PropertyCheckResult shrinkAndCreateCheckResult(Consumer<ReportEntry> reporter, int countChecks,
														   int countTries, List<Shrinkable> shrinkables, AssertionError error) {
		List<Object> originalParams = extractParams(shrinkables);

		PropertyShrinker shrinker = new PropertyShrinker(shrinkables, configuration.getShrinkingMode(), reporter, configuration.getReporting());

		Falsifier<List> forAllFalsifier = checkedFunction::test;
		PropertyShrinkingResult shrinkingResult = shrinker.shrink(forAllFalsifier, error);

		@SuppressWarnings("unchecked")
		List<Object> shrunkParams = shrinkingResult.values();
		Throwable throwable = shrinkingResult.throwable().orElse(null);
		return PropertyCheckResult.falsified(configuration.getStereotype(), name, countTries, countChecks, configuration.getSeed(), shrunkParams, originalParams, throwable);
	}

}
