package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.support.*;

class RandomizedParameterGenerator {
	private final TypeUsage typeUsage;
	private final List<Arbitrary<Object>> arbitraries;
	private final int genSize;

	RandomizedParameterGenerator(MethodParameter parameter, Set<Arbitrary<Object>> arbitraries, int genSize) {
		this.typeUsage = TypeUsageImpl.forParameter(parameter);
		this.arbitraries = new ArrayList<>(arbitraries);
		this.genSize = genSize;
	}

	Shrinkable<Object> next(Random random, Map<TypeUsage, Arbitrary<Object>> arbitrariesCache) {
		RandomGenerator<Object> selectedGenerator = selectGenerator(random, arbitrariesCache);
		return selectedGenerator.next(random);
	}

	private RandomGenerator<Object> selectGenerator(Random random, Map<TypeUsage, Arbitrary<Object>> arbitrariesCache) {
		if (arbitrariesCache.containsKey(typeUsage)) {
			return arbitrariesCache.get(typeUsage).generator(genSize);
		}
		int index = arbitraries.size() == 1 ? 0 : random.nextInt(arbitraries.size());
		Arbitrary<Object> selectedArbitrary = arbitraries.get(index);
		arbitrariesCache.put(typeUsage, selectedArbitrary);
		return selectedArbitrary.generator(genSize);
	}
}
