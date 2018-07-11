package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.support.*;

import java.util.*;

public class RandomParameterGenerator {
	private final TypeUsage typeUsage;
	private final List<RandomGenerator> generators;

	public RandomParameterGenerator(MethodParameter parameter, Set<RandomGenerator> generators) {
		this.typeUsage = TypeUsage.forParameter(parameter);
		this.generators = new ArrayList<>(generators);
	}

	public Shrinkable next(Random random) {
		int index = generators.size() == 1 ? 0 : random.nextInt(generators.size());
		return generators.get(index).next(random);
	}
}
