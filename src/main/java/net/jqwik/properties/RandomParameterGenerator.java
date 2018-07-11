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

	public Shrinkable next(Random random, Map<TypeUsage, RandomGenerator> generatorsCache) {
		RandomGenerator selectedGenerator = selectGenerator(random, generatorsCache);
		return selectedGenerator.next(random);
	}

	private RandomGenerator selectGenerator(Random random, Map<TypeUsage, RandomGenerator> generatorsCache) {
		if (generatorsCache.containsKey(typeUsage)) {
			return generatorsCache.get(typeUsage);
		}
		int index = generators.size() == 1 ? 0 : random.nextInt(generators.size());
		RandomGenerator selectedGenerator = generators.get(index);
		generatorsCache.put(typeUsage, selectedGenerator);
		return selectedGenerator;
	}
}
