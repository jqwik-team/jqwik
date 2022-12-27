package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;
import net.jqwik.engine.support.types.*;

class RandomizedParameterGenerator {
	private final TypeUsage typeUsage;
	private final List<Arbitrary<Object>> arbitraries;
	private final int genSize;
	private final boolean withEdgeCases;

	RandomizedParameterGenerator(MethodParameter parameter, Set<Arbitrary<Object>> arbitraries, int genSize, boolean withEdgeCases) {
		this.typeUsage = TypeUsageImpl.forParameter(parameter);
		this.arbitraries = new ArrayList<>(arbitraries);
		this.genSize = genSize;
		this.withEdgeCases = withEdgeCases;
	}

	Shrinkable<Object> next(JqwikRandom random, Map<TypeUsage, Arbitrary<Object>> arbitrariesCache) {
		RandomGenerator<Object> selectedGenerator = selectGenerator(random, arbitrariesCache);
		return selectedGenerator.next(random);
	}

	private RandomGenerator<Object> selectGenerator(JqwikRandom random, Map<TypeUsage, Arbitrary<Object>> arbitrariesCache) {
		if (arbitrariesCache.containsKey(typeUsage)) {
			Arbitrary<Object> arbitrary = arbitrariesCache.get(typeUsage);
			return getGenerator(arbitrary);
		}
		int index = arbitraries.size() == 1 ? 0 : random.nextInt(arbitraries.size());
		Arbitrary<Object> selectedArbitrary = arbitraries.get(index);
		arbitrariesCache.put(typeUsage, selectedArbitrary);
		return getGenerator(selectedArbitrary);
	}

	private RandomGenerator<Object> getGenerator(Arbitrary<Object> arbitrary) {
		return arbitrary.generator(genSize, withEdgeCases);
	}
}
