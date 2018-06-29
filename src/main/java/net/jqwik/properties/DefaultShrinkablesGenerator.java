package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.support.*;

import java.util.*;
import java.util.stream.*;

public class DefaultShrinkablesGenerator implements ShrinkablesGenerator {

	public static DefaultShrinkablesGenerator forParameters(List<MethodParameter> parameters, ArbitraryResolver arbitraryResolver, int genSize) {
		List<RandomGenerator> generators = parameters.stream()
													.map(parameter -> findArbitrary(arbitraryResolver, parameter))
													.map(arbitrary -> arbitrary.generator(genSize))
													.collect(Collectors.toList());
		return new DefaultShrinkablesGenerator(generators);
	}

	private static Arbitrary<Object> findArbitrary(ArbitraryResolver arbitraryResolver, MethodParameter parameter) {
		Optional<Arbitrary<Object>> arbitraryOptional = arbitraryResolver.forParameter(parameter);
		return arbitraryOptional.orElseThrow(() -> new CannotFindArbitraryException(parameter));
	}

	private final List<RandomGenerator> generators;

	private DefaultShrinkablesGenerator(List<RandomGenerator> generators) {
		this.generators = generators;
	}

	@Override
	public List<Shrinkable> next(Random random) {
		return generators.stream().map(generator -> generator.next(random)).collect(Collectors.toList());
	}


}
