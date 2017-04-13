package net.jqwik.properties;

import org.opentest4j.*;

import java.util.*;
import java.util.function.*;

public class GenericProperty {

	private final String name;
	private final List<Arbitrary> arbitraries;
	private final Function<List<Object>, Boolean> forAllFunction;

	public GenericProperty(String name, List<Arbitrary> arbitraries, Function<List<Object>, Boolean> forAllFunction) {
		this.name = name;
		this.arbitraries = arbitraries;
		this.forAllFunction = forAllFunction;
	}

	public PropertyCheckResult check(int tries, long seed) {
		Arbitrary<?> a1 = arbitraries.get(0);
		Generator<?> g1 = a1.generator(seed, tries);
		int countChecks = 0;
		for (int countTries = 1; countTries <= tries; countTries++) {
			List<Object> params = generateParameters(g1);
			try {
				boolean check = forAllFunction.apply(params);
				countChecks++;
				if (!check) {
					return PropertyCheckResult.falsified(name, countTries, countChecks, seed, params);
				}
			} catch (TestAbortedException tae) {
				continue;
			}
		}
		return PropertyCheckResult.satisfied(name, tries, countChecks, seed);
	}

	protected List<Object> generateParameters(Generator<?> g1) {
		Object p1 = g1.next();
		List<Object> params = new ArrayList<>();
		params.add(p1);
		return params;
	}
}
