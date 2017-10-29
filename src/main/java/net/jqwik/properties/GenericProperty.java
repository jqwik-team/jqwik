package net.jqwik.properties;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import org.junit.platform.commons.util.BlacklistedExceptions;
import org.junit.platform.engine.reporting.ReportEntry;
import org.opentest4j.TestAbortedException;

import net.jqwik.api.*;
import net.jqwik.support.JqwikStringSupport;

public class GenericProperty {

	private final String name;
	private final List<Arbitrary> arbitraries;
	private final Predicate<List<Object>> forAllPredicate;

	public GenericProperty(String name, List<Arbitrary> arbitraries, CheckedFunction forAllPredicate) {
		this.name = name;
		this.arbitraries = arbitraries;
		this.forAllPredicate = forAllPredicate;
	}

	public PropertyCheckResult check(int tries, int maxDiscardRatio, long seed, ShrinkingMode shrinkingMode, ReportingMode reportingMode,
			Consumer<ReportEntry> publisher) {
		Random random = new Random(seed);
		List<RandomGenerator> generators = arbitraries.stream().map(a1 -> a1.generator(tries)).collect(Collectors.toList());
		int maxTries = generators.isEmpty() ? 1 : tries;
		int countChecks = 0;
		for (int countTries = 1; countTries <= maxTries; countTries++) {
			List<Shrinkable> shrinkableParams = generateParameters(generators, random);
			try {
				countChecks++;
				if (!testPredicate(shrinkableParams, reportingMode, publisher)) {
					return shrinkAndCreateCheckResult(shrinkingMode, seed, countChecks, countTries, shrinkableParams, null);
				}
			} catch (AssertionError ae) {
				return shrinkAndCreateCheckResult(shrinkingMode, seed, countChecks, countTries, shrinkableParams, ae);
			} catch (TestAbortedException tae) {
				countChecks--;
				continue;
			} catch (Throwable throwable) {
				BlacklistedExceptions.rethrowIfBlacklisted(throwable);
				return PropertyCheckResult.erroneous(name, countTries, countChecks, seed, extractParams(shrinkableParams), throwable);
			}
		}
		if (countChecks == 0 || maxDiscardRatioExceeded(countChecks, maxTries, maxDiscardRatio))
			return PropertyCheckResult.exhausted(name, maxTries, countChecks, seed);
		return PropertyCheckResult.satisfied(name, maxTries, countChecks, seed);
	}

	private boolean testPredicate(List<Shrinkable> shrinkableParams, ReportingMode reportingMode, Consumer<ReportEntry> publisher) {
		List<Object> plainParams = extractParams(shrinkableParams);
		if (reportingMode == ReportingMode.GENERATED) {
			publisher.accept(ReportEntry.from("generated", JqwikStringSupport.displayString(plainParams)));
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
	private PropertyCheckResult shrinkAndCreateCheckResult(ShrinkingMode shrinkingMode, long seed, int countChecks, int countTries,
			List<Shrinkable> shrinkables, AssertionError error) {
		List<Object> originalParams = extractParams(shrinkables);
		if (shrinkingMode == ShrinkingMode.OFF) {
			return PropertyCheckResult.falsified(name, countTries, countChecks, seed, originalParams, originalParams, error);
		}
		ParameterListShrinker shrinker = new ParameterListShrinker(shrinkables);
		ShrinkResult<List<Shrinkable>> shrinkResult = shrinker.shrink(forAllPredicate, error);
		List<Object> shrunkParams = extractParams(shrinkResult.shrunkValue());
		Throwable throwable = shrinkResult.throwable().orElse(null);
		return PropertyCheckResult.falsified(name, countTries, countChecks, seed, shrunkParams, originalParams, throwable);
	}

	private List<Shrinkable> generateParameters(List<RandomGenerator> generators, Random random) {
		return generators.stream().map(generator -> generator.next(random)).collect(Collectors.toList());
	}
}
