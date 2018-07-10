package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.support.*;

import java.util.*;

public class RandomParameterGenerator {
	private final MethodParameter parameter;
	private final Set<RandomGenerator> generators;

	public RandomParameterGenerator(MethodParameter parameter, Set<RandomGenerator> generators) {
		this.parameter = parameter;
		this.generators = generators;
	}

	public Shrinkable next(Random random) {
		return generators.iterator().next().next(random);
	}
}
