package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

public class GenericProperty {

	private final String name;
	private final List<Arbitrary> arbitraries;
	private final Function<List<?>, Boolean> forAllFunction;

	public GenericProperty(String name, List<Arbitrary> arbitraries, Function<List<?>, Boolean> forAllFunction) {
		this.name = name;
		this.arbitraries = arbitraries;
		this.forAllFunction = forAllFunction;
	}

	public PropertyCheckResult check(int tries, long seed) {
		Arbitrary<?> a1 = arbitraries.get(0);
		Generator<?> g1 = a1.generator(seed, tries);
		for (int currentTry = 1; currentTry <= tries; currentTry++) {
			List<Object> params = generateParameters(g1);
			boolean check = forAllFunction.apply(params);
			if (!check) {
				return PropertyCheckResult.falsified(name, currentTry, seed, params);
			}
		}
		return PropertyCheckResult.satisfied(name, tries, seed);
	}

	protected List<Object> generateParameters(Generator<?> g1) {
		Object p1 = g1.next();
		List<Object> params = new ArrayList<>();
		params.add(p1);
		return params;
	}
}
