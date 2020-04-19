package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.support.*;

import static java.lang.Math.*;

public class RandomizedShrinkablesGenerator implements ForAllParametersGenerator {

	public static RandomizedShrinkablesGenerator forParameters(
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver,
		Random random,
		int genSize,
		EdgeCasesMode edgeCasesMode
	) {

		List<EdgeCases<Object>> listOfEdgeCases = listOfEdgeCases(parameters, arbitraryResolver, edgeCasesMode);

		return new RandomizedShrinkablesGenerator(
			randomShrinkablesGenerator(parameters, arbitraryResolver, genSize),
			new EdgeCasesGenerator(listOfEdgeCases),
			edgeCasesMode,
			calculateBaseToEdgeCaseRatio(listOfEdgeCases, genSize),
			random
		);
	}

	private static PurelyRandomShrinkablesGenerator randomShrinkablesGenerator(
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver,
		int genSize
	) {
		List<RandomizedParameterGenerator> parameterGenerators = parameterGenerators(parameters, arbitraryResolver, genSize);
		return new PurelyRandomShrinkablesGenerator(parameterGenerators);
	}

	private static List<RandomizedParameterGenerator> parameterGenerators(
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver,
		int genSize
	) {
		return parameters.stream()
						 .map(parameter -> resolveParameter(arbitraryResolver, parameter, genSize))
						 .collect(Collectors.toList());
	}

	private static List<EdgeCases<Object>> listOfEdgeCases(
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver,
		EdgeCasesMode edgeCasesMode
	) {
		List<EdgeCases<Object>> listOfEdgeCases = Collections.emptyList();
		if (edgeCasesMode != EdgeCasesMode.NONE) {
			listOfEdgeCases = parameters
				.stream()
				.map(parameter -> resolveEdgeCases(arbitraryResolver, parameter))
				.collect(Collectors.toList());
		}
		return listOfEdgeCases;
	}

	private static int calculateBaseToEdgeCaseRatio(List<EdgeCases<Object>> edgeCases, int genSize) {
		int countEdgeCases = edgeCases.stream().mapToInt(EdgeCases::size).reduce(1, (a, b) -> max(a * b, 1));
		return min(
			max(genSize / countEdgeCases, 3) - 1,
			10
		);
	}

	private static EdgeCases<Object> resolveEdgeCases(
		ArbitraryResolver arbitraryResolver,
		MethodParameter parameter
	) {
		List<EdgeCases<Object>> edgeCases = resolveArbitraries(arbitraryResolver, parameter)
												.stream()
												.map(Arbitrary::edgeCases)
												.collect(Collectors.toList());
		return EdgeCases.concat(edgeCases);
	}

	private static RandomizedParameterGenerator resolveParameter(
		ArbitraryResolver arbitraryResolver,
		MethodParameter parameter,
		int genSize
	) {
		Set<Arbitrary<Object>> arbitraries = resolveArbitraries(arbitraryResolver, parameter);
		return new RandomizedParameterGenerator(parameter, arbitraries, genSize);
	}

	private static Set<Arbitrary<Object>> resolveArbitraries(ArbitraryResolver arbitraryResolver, MethodParameter parameter) {
		Set<Arbitrary<Object>> arbitraries =
			arbitraryResolver.forParameter(parameter).stream()
							 .map(GenericArbitrary::new)
							 .collect(Collectors.toSet());
		if (arbitraries.isEmpty()) {
			throw new CannotFindArbitraryException(TypeUsageImpl.forParameter(parameter), parameter.getAnnotation(ForAll.class));
		}
		return arbitraries;
	}

	private final PurelyRandomShrinkablesGenerator randomGenerator;
	private final EdgeCasesGenerator edgeCasesGenerator;
	private final EdgeCasesMode edgeCasesMode;
	private final int baseToEdgeCaseRatio;
	private final Random random;

	private boolean edgeCasesGenerated = false;

	private RandomizedShrinkablesGenerator(
		PurelyRandomShrinkablesGenerator randomGenerator,
		EdgeCasesGenerator edgeCasesGenerator,
		EdgeCasesMode edgeCasesMode,
		int baseToEdgeCaseRatio,
		Random random
	) {
		this.randomGenerator = randomGenerator;
		this.edgeCasesGenerator = edgeCasesGenerator;
		this.edgeCasesMode = edgeCasesMode;
		this.baseToEdgeCaseRatio = baseToEdgeCaseRatio;
		this.random = random;
	}

	@Override
	public boolean hasNext() {
		// Randomized generation should always be able to generate a next set of values
		return true;
	}

	@Override
	public List<Shrinkable<Object>> next() {
		if (!edgeCasesGenerated) {
			if (edgeCasesMode.generateFirst()) {
				if (edgeCasesGenerator.hasNext()) {
					return edgeCasesGenerator.next();
				} else {
					edgeCasesGenerated = true;
				}
			}
			if (edgeCasesMode.mixIn()) {
				if (shouldGenerateEdgeCase(random)) {
					if (edgeCasesGenerator.hasNext()) {
						return edgeCasesGenerator.next();
					} else {
						edgeCasesGenerated = true;
					}
				}
			}
		}
		return randomGenerator.generateNext(random);
	}

	private boolean shouldGenerateEdgeCase(Random localRandom) {
		return localRandom.nextInt(baseToEdgeCaseRatio + 1) == 0;
	}

}
