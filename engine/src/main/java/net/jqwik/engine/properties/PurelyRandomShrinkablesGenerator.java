package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

class PurelyRandomShrinkablesGenerator {

	private final List<RandomizedParameterGenerator> parameterGenerators;

	PurelyRandomShrinkablesGenerator(List<RandomizedParameterGenerator> parameterGenerators) {
		this.parameterGenerators = parameterGenerators;
	}

	List<Shrinkable<Object>> generateNext(Random random) {
		Map<TypeUsage, Arbitrary<Object>> generatorsCache = new LinkedHashMap<>();
		return parameterGenerators
				   .stream()
				   .map(generator -> generator.next(random, generatorsCache))
				   .collect(Collectors.toList());
	}

}
