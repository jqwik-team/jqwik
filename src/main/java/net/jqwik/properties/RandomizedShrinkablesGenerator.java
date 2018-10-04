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
		List<RandomParameterGenerator> parameterGenerators =
			parameters.stream()
					  .map(parameter -> resolveParameter(arbitraryResolver, parameter, genSize))
					  .collect(Collectors.toList());

		return new RandomizedShrinkablesGenerator(parameterGenerators, random);
	}

	private static RandomParameterGenerator resolveParameter(ArbitraryResolver arbitraryResolver, MethodParameter parameter, int genSize) {
		Set<RandomGenerator> generators =
			arbitraryResolver.forParameter(parameter).stream()
							 .map(GenericArbitrary::new)
							 .map(arbitrary -> arbitrary.generator(genSize))
							 .collect(Collectors.toSet());
		if (generators.isEmpty()) {
			throw new CannotFindArbitraryException(parameter);
		}
		return new RandomParameterGenerator(parameter, generators);
	}

	private final List<RandomParameterGenerator> parameterGenerators;
	private final Random random;

	private RandomizedShrinkablesGenerator(List<RandomParameterGenerator> parameterGenerators, Random random) {
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

}
