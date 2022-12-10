package net.jqwik.engine.properties;

import java.util.*;
import java.util.logging.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.random.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.support.*;
import net.jqwik.engine.support.types.*;

import static java.lang.Math.*;

public class RandomizedShrinkablesGenerator implements ForAllParametersGenerator {

	private static final Logger LOG = Logger.getLogger(RandomizedShrinkablesGenerator.class.getName());

	public static RandomizedShrinkablesGenerator forParameters(
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver,
		JqwikRandom random,
		int genSize,
		EdgeCasesMode edgeCasesMode
	) {

		List<EdgeCases<Object>> listOfEdgeCases = listOfEdgeCases(parameters, arbitraryResolver, edgeCasesMode, genSize);
		int edgeCasesTotal = calculateEdgeCasesTotal(listOfEdgeCases);

		logEdgeCasesOutnumberTriesIfApplicable(genSize, edgeCasesTotal);

		return new RandomizedShrinkablesGenerator(
			randomShrinkablesGenerator(parameters, arbitraryResolver, genSize, edgeCasesMode.activated()),
			new EdgeCasesGenerator(listOfEdgeCases),
			edgeCasesMode,
			edgeCasesTotal,
			calculateBaseToEdgeCaseRatio(listOfEdgeCases, genSize),
			random.split().saveState()
		);
	}

	private static void logEdgeCasesOutnumberTriesIfApplicable(int genSize, int edgeCasesTotal) {
		int logEdgeCasesExceedTriesLimit = max(genSize, 100);
		if (edgeCasesTotal >= logEdgeCasesExceedTriesLimit && genSize > 1) {
			String message = String.format(
				"Edge case generation exceeds number of tries. Stopped after %s generated cases.",
				edgeCasesTotal
			);
			LOG.log(Level.INFO, message);
		}
	}

	private static int calculateEdgeCasesTotal(final List<EdgeCases<Object>> listOfEdgeCases) {
		if (listOfEdgeCases.isEmpty()) {
			return 0;
		}
		return listOfEdgeCases.stream().mapToInt(EdgeCases::size).reduce(1, (a, b) -> a * b);
	}

	private static PurelyRandomShrinkablesGenerator randomShrinkablesGenerator(
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver,
		int genSize,
		boolean withEdgeCases
	) {
		List<RandomizedParameterGenerator> parameterGenerators = parameterGenerators(parameters, arbitraryResolver, genSize, withEdgeCases);
		return new PurelyRandomShrinkablesGenerator(parameterGenerators);
	}

	private static List<RandomizedParameterGenerator> parameterGenerators(
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver,
		int genSize,
		boolean withEdgeCases
	) {
		return parameters.stream()
						 .map(parameter -> resolveParameter(arbitraryResolver, parameter, genSize, withEdgeCases))
						 .collect(Collectors.toList());
	}

	private static List<EdgeCases<Object>> listOfEdgeCases(
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver,
		EdgeCasesMode edgeCasesMode,
		int genSize
	) {
		List<EdgeCases<Object>> listOfEdgeCases = new ArrayList<>();

		if (edgeCasesMode.activated() && !parameters.isEmpty()) {
			int maxEdgeCasesNextParameter = genSize;
			for (MethodParameter parameter : parameters) {
				EdgeCases<Object> edgeCases = resolveEdgeCases(arbitraryResolver, parameter, maxEdgeCasesNextParameter);
				// If a single parameter has no edge cases the combination of parameters have no edge cases
				if (edgeCases.isEmpty()) {
					return Collections.emptyList();
				}
				listOfEdgeCases.add(edgeCases);
				maxEdgeCasesNextParameter = calculateNextParamMaxEdgeCases(maxEdgeCasesNextParameter, edgeCases.size());
			}
		}
		return listOfEdgeCases;
	}

	private static <T> int calculateNextParamMaxEdgeCases(int maxEdgeCases, int baseCasesSize) {
		int maxDerivedEdgeCases = Math.max(1, maxEdgeCases / baseCasesSize);
		// When in doubt generate a few more edge cases
		if (maxEdgeCases % baseCasesSize > 0) {
			maxDerivedEdgeCases += 1;
		}
		return maxDerivedEdgeCases;
	}

	private static int calculateBaseToEdgeCaseRatio(List<EdgeCases<Object>> edgeCases, int genSize) {
		int countEdgeCases = edgeCases.stream().mapToInt(EdgeCases::size).reduce(1, (a, b) -> max(a * b, 1));
		return EdgeCasesGenerator.calculateBaseToEdgeCaseRatio(genSize, countEdgeCases);
	}

	private static EdgeCases<Object> resolveEdgeCases(
		ArbitraryResolver arbitraryResolver,
		MethodParameter parameter,
		int maxEdgeCases
	) {
		List<EdgeCases<Object>> edgeCases = resolveArbitraries(arbitraryResolver, parameter)
			.stream()
			.map(objectArbitrary -> objectArbitrary.edgeCases(maxEdgeCases))
			.collect(Collectors.toList());
		return EdgeCasesSupport.concat(edgeCases, maxEdgeCases);
	}

	private static RandomizedParameterGenerator resolveParameter(
		ArbitraryResolver arbitraryResolver,
		MethodParameter parameter,
		int genSize,
		boolean withEdgeCases
	) {
		Set<Arbitrary<Object>> arbitraries = resolveArbitraries(arbitraryResolver, parameter);

		// This logging only makes sense if arbitraries get the capability to describe themselves
		// Supplier<String> message = () -> String.format("Parameter %s generated by arbitraries %s", parameter.getRawParameter(), arbitraries);
		// LOG.log(Level.INFO, message);

		return new RandomizedParameterGenerator(parameter, arbitraries, genSize, withEdgeCases);
	}

	private static Set<Arbitrary<Object>> resolveArbitraries(ArbitraryResolver arbitraryResolver, MethodParameter parameter) {
		Set<Arbitrary<Object>> arbitraries =
			arbitraryResolver.forParameter(parameter).stream()
							 .map(Arbitrary::asGeneric)
							 .collect(CollectorsSupport.toLinkedHashSet());
		if (arbitraries.isEmpty()) {
			throw new CannotFindArbitraryException(TypeUsageImpl.forParameter(parameter), parameter.getAnnotation(ForAll.class));
		}
		return arbitraries;
	}

	private final PurelyRandomShrinkablesGenerator randomGenerator;
	private final EdgeCasesGenerator edgeCasesGenerator;
	private final EdgeCasesMode edgeCasesMode;
	private final int edgeCasesTotal;
	private final int baseToEdgeCaseRatio;
	private final JqwikRandomState seed;
	private JqwikRandom random;

	private boolean allEdgeCasesGenerated = false;
	private int edgeCasesTried = 0;

	private RandomizedShrinkablesGenerator(
		PurelyRandomShrinkablesGenerator randomGenerator,
		EdgeCasesGenerator edgeCasesGenerator,
		EdgeCasesMode edgeCasesMode,
		int edgeCasesTotal,
		int baseToEdgeCaseRatio,
		JqwikRandomState seed
	) {
		this.randomGenerator = randomGenerator;
		this.edgeCasesGenerator = edgeCasesGenerator;
		this.edgeCasesMode = edgeCasesMode;
		this.edgeCasesTotal = edgeCasesTotal;
		this.baseToEdgeCaseRatio = baseToEdgeCaseRatio;
		this.seed = seed;
		this.random = SourceOfRandomness.newRandom(seed);
	}

	@Override
	public boolean hasNext() {
		// Randomized generation should always be able to generate a next set of values
		return true;
	}

	@Override
	public List<Shrinkable<Object>> next() {
		if (!allEdgeCasesGenerated) {
			if (edgeCasesMode.generateFirst()) {
				if (edgeCasesGenerator.hasNext()) {
					edgeCasesTried++;
					return edgeCasesGenerator.next();
				} else {
					allEdgeCasesGenerated = true;
				}
			}
			if (edgeCasesMode.mixIn()) {
				if (shouldGenerateEdgeCase(random)) {
					if (edgeCasesGenerator.hasNext()) {
						edgeCasesTried++;
						return edgeCasesGenerator.next();
					} else {
						allEdgeCasesGenerated = true;
					}
				}
			}
		}
		return randomGenerator.generateNext(random);
	}

	@Override
	public int edgeCasesTotal() {
		return edgeCasesTotal;
	}

	@Override
	public int edgeCasesTried() {
		return edgeCasesTried;
	}

	@Override
	public void reset() {
		random = SourceOfRandomness.newRandom(seed);
	}

	private boolean shouldGenerateEdgeCase(JqwikRandom localRandom) {
		return localRandom.nextInt(baseToEdgeCaseRatio + 1) == 0;
	}

}
