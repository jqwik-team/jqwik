package net.jqwik.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.support.*;

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
		Set<RandomGenerator> generators =
			arbitraryResolver.forParameter(parameter).stream()
							 .map(GenericArbitrary::new)
							 .map(arbitrary -> arbitrary.generator(genSize))
							 .collect(Collectors.toSet());
		if (generators.isEmpty()) {
			throw new CannotFindArbitraryException(parameter);
		}
		return new RandomizedParameterGenerator(parameter, generators);
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
		Map<TypeUsage, RandomGenerator> generatorsCache = new HashMap<>();
		return parameterGenerators
				   .stream()
				   .map(generator -> generator.next(random, generatorsCache))
				   .collect(Collectors.toList());
	}

	private static class RandomizedParameterGenerator {
		private final TypeUsage typeUsage;
		private final List<RandomGenerator> generators;

		private RandomizedParameterGenerator(MethodParameter parameter, Set<RandomGenerator> generators) {
			this.typeUsage = TypeUsage.forParameter(parameter);
			this.generators = new ArrayList<>(generators);
		}

		private Shrinkable next(Random random, Map<TypeUsage, RandomGenerator> generatorsCache) {
			RandomGenerator selectedGenerator = selectGenerator(random, generatorsCache);
			selectedGenerator.reset();
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
}
