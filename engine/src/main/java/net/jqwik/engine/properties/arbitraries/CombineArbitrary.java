package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

public class CombineArbitrary<R> implements Arbitrary<R> {

	private final Function<List<Object>, R> combinator;
	private final List<Arbitrary<Object>> arbitraries;

	@SuppressWarnings("unchecked")
	public CombineArbitrary(Function<List<Object>, R> combinator, Arbitrary<?>[] arbitraries) {
		this.combinator = combinator;
		this.arbitraries = Arrays.asList((Arbitrary<Object>[]) arbitraries);
	}

	@Override
	public RandomGenerator<R> generator(int genSize) {
		return combineGenerator(genSize, combinator, arbitraries);
	}

	@Override
	public RandomGenerator<R> generatorWithEmbeddedEdgeCases(int genSize) {
		return combineGeneratorWithEmbeddedEdgeCases(genSize, combinator, arbitraries);
	}

	@Override
	public Optional<ExhaustiveGenerator<R>> exhaustive(long maxNumberOfSamples) {
		return combineExhaustive(
			arbitraries,
			combinator,
			maxNumberOfSamples
		);
	}

	@Override
	public boolean isGeneratorMemoizable() {
		return isCombinedGeneratorMemoizable(arbitraries);
	}

	@Override
	public EdgeCases<R> edgeCases(int maxEdgeCases) {
		return combineEdgeCases(
			arbitraries,
			combinator,
			maxEdgeCases
		);
	}

	private static boolean isCombinedGeneratorMemoizable(List<Arbitrary<Object>> arbitraries) {
		return arbitraries.stream().allMatch(Arbitrary::isGeneratorMemoizable);
	}

	private RandomGenerator<R> combineGenerator(
		int genSize,
		Function<List<Object>, R> combineFunction,
		List<Arbitrary<Object>> arbitraries
	) {
		List<RandomGenerator<?>> generators = arbitraries.stream()
													.map(a -> a.generator(genSize))
													.collect(Collectors.toList());
		return random -> {
			List<Shrinkable<Object>> shrinkables = generateShrinkables(generators, random);
			return combineShrinkables(shrinkables, combineFunction);
		};
	}

	private RandomGenerator<R> combineGeneratorWithEmbeddedEdgeCases(
		int genSize,
		Function<List<Object>, R> combineFunction,
		List<Arbitrary<Object>> arbitraries
	) {
		List<RandomGenerator<?>> generators =
			arbitraries.stream()
				  .map(a -> a.generatorWithEmbeddedEdgeCases(genSize))
				  .collect(Collectors.toList());
		return random -> {
			List<Shrinkable<Object>> shrinkables = generateShrinkables(generators, random);
			return combineShrinkables(shrinkables, combineFunction);
		};
	}

	@SuppressWarnings("unchecked")
	private static <T> List<Shrinkable<T>> generateShrinkables(List<RandomGenerator<?>> generators, Random random) {
		List<Shrinkable<T>> list = new ArrayList<>();
		for (RandomGenerator<?> generator : generators) {
			list.add((Shrinkable<T>) generator.next(random));
		}
		return list;
	}

	private Shrinkable<R> combineShrinkables(
		List<Shrinkable<Object>> shrinkables, Function<List<Object>, R> combineFunction
	) {
		return new CombinedShrinkable<>(shrinkables, combineFunction);
	}

	private Optional<ExhaustiveGenerator<R>> combineExhaustive(
		List<Arbitrary<Object>> arbitraries,
		Function<List<Object>, R> combineFunction,
		long maxNumberOfSamples
	) {
		return ExhaustiveGenerators.combine(arbitraries, combineFunction, maxNumberOfSamples);
	}

	private EdgeCases<R> combineEdgeCases(
		final List<Arbitrary<Object>> arbitraries,
		final Function<List<Object>, R> combineFunction,
		int maxEdgeCases
	) {
		return EdgeCasesSupport.combine(arbitraries, combineFunction, maxEdgeCases);
	}

}
