package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;

public class RandomizedShrinkablesGenerator implements ShrinkablesGenerator {

	public static RandomizedShrinkablesGenerator forParameters(
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver,
		Random random,
		int genSize
	) {
		List<RandomizedParameterGenerator> parameterGenerators =
			parameters.stream()
					  .map(parameter -> resolveParameter(arbitraryResolver, parameter, genSize))
					  .collect(Collectors.toList());

		return new RandomizedShrinkablesGenerator(parameterGenerators, random);
	}

	private static RandomizedParameterGenerator resolveParameter(ArbitraryResolver arbitraryResolver, MethodParameter parameter, int genSize) {
		Set<Arbitrary> arbitraries =
			arbitraryResolver.forParameter(parameter).stream()
							 .map(GenericArbitrary::new)
							 .collect(Collectors.toSet());
		if (arbitraries.isEmpty()) {
			throw new CannotFindArbitraryException(TypeUsage.forParameter(parameter), parameter.getAnnotation(ForAll.class));
		}
		return new RandomizedParameterGenerator(parameter, arbitraries, genSize);
	}

	private final List<RandomizedParameterGenerator> parameterGenerators;
	private final Random random;

	private RandomizedShrinkablesGenerator(List<RandomizedParameterGenerator> parameterGenerators, Random random) {
		this.parameterGenerators = parameterGenerators;
		this.random = random;
	}

	@Override
	public boolean hasNext() {
		// Randomized generation should always be able to generate a next set of values
		return true;
	}

	@Override
	public List<Shrinkable> next() {
		Map<TypeUsage, Arbitrary> generatorsCache = new HashMap<>();
		return parameterGenerators
				   .stream()
				   .map(generator -> generator.next(random, generatorsCache))
				   .collect(Collectors.toList());
	}

	private static class RandomizedParameterGenerator {
		private final TypeUsage typeUsage;
		private final List<Arbitrary> arbitraries;
		private final int genSize;

		private RandomizedParameterGenerator(MethodParameter parameter, Set<Arbitrary> arbitraries, int genSize) {
			this.typeUsage = TypeUsage.forParameter(parameter);
			this.arbitraries = new ArrayList<>(arbitraries);
			this.genSize = genSize;
		}

		private Shrinkable next(Random random, Map<TypeUsage, Arbitrary> arbitrariesCache) {
			RandomGenerator selectedGenerator = selectGenerator(random, arbitrariesCache);
			return selectedGenerator.next(random);
		}

		private RandomGenerator selectGenerator(Random random, Map<TypeUsage, Arbitrary> arbitrariesCache) {
			if (arbitrariesCache.containsKey(typeUsage)) {
				return arbitrariesCache.get(typeUsage).generator(genSize);
			}
			int index = arbitraries.size() == 1 ? 0 : random.nextInt(arbitraries.size());
			Arbitrary selectedArbitrary = arbitraries.get(index);
			arbitrariesCache.put(typeUsage, selectedArbitrary);
			return selectedArbitrary.generator(genSize);
		}
	}
}
