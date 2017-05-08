package net.jqwik.properties;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.util.*;
import org.opentest4j.*;

import net.jqwik.properties.shrinking.*;

public class GenericProperty {

	private final String name;
	private final List<Arbitrary> arbitraries;
	private final Predicate<List<Object>> forAllPredicate;

	public GenericProperty(String name, List<Arbitrary> arbitraries, CheckedFunction forAllPredicate) {
		this.name = name;
		this.arbitraries = arbitraries;
		this.forAllPredicate = forAllPredicate;
	}

	public PropertyCheckResult check(int tries, long seed) {
		Random random = new Random(seed);
		List<RandomGenerator> generators = arbitraries.stream().map(a1 -> a1.generator(tries)).collect(Collectors.toList());
		int maxTries = generators.isEmpty() ? 1 : tries;
		int countChecks = 0;
		for (int countTries = 1; countTries <= maxTries; countTries++) {
			List<Object> params = generateParameters(generators, random);
			try {
				countChecks++;
				boolean check = forAllPredicate.test(params);
				if (!check) {
					return shrinkAndCreateCheckResult(seed, countChecks, countTries, params, null);
				}
			} catch (AssertionError ae) {
				return shrinkAndCreateCheckResult(seed, countChecks, countTries, params, ae);
			} catch (TestAbortedException tae) {
				countChecks--;
				continue;
			} catch (Throwable throwable) {
				BlacklistedExceptions.rethrowIfBlacklisted(throwable);
				return PropertyCheckResult.erroneous(name, countTries, countChecks, seed, params, throwable);
			}
		}
		if (countChecks == 0)
			return PropertyCheckResult.exhausted(name, maxTries, seed);
		return PropertyCheckResult.satisfied(name, maxTries, countChecks, seed);
	}

	private PropertyCheckResult shrinkAndCreateCheckResult(long seed, int countChecks, int countTries, List<Object> params,
			AssertionError error) {
		FalsifiedShrinker falsifiedShrinker = new FalsifiedShrinker(arbitraries, forAllPredicate);
		ShrinkResult<List<Object>> shrinkingResult = falsifiedShrinker.shrink(params, error);
		AssertionError throwable = shrinkingResult.error().orElse(null);
		return PropertyCheckResult.falsified(name, countTries, countChecks, seed, shrinkingResult.value(), throwable);
	}

	private List<Object> generateParameters(List<RandomGenerator> generators, Random random) {
		return generators.stream().map(generator -> generator.next(random)).collect(Collectors.toList());
	}
}
