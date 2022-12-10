package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

public class CombineArbitrary<R> implements Arbitrary<R> {

	private final Function<List<Object>, R> combinator;
	private final List<Arbitrary<Object>> arbitraries;

	@SuppressWarnings("unchecked")
	public CombineArbitrary(Function<List<Object>, R> combinator, Arbitrary<?>... arbitraries) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CombineArbitrary<?> that = (CombineArbitrary<?>) o;
		if (!arbitraries.equals(that.arbitraries)) return false;
		return LambdaSupport.areEqual(combinator, that.combinator);
	}

	@Override
	public int hashCode() {
		return HashCodeSupport.hash(arbitraries);
	}

	private boolean isCombinedGeneratorMemoizable(List<Arbitrary<Object>> arbitraries) {
		return arbitraries.stream().allMatch(Arbitrary::isGeneratorMemoizable);
	}

	private RandomGenerator<R> combineGenerator(
		int genSize,
		Function<List<Object>, R> combineFunction,
		List<Arbitrary<Object>> arbitraries
	) {
		List<RandomGenerator<Object>> generators = arbitraries.stream()
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
		List<RandomGenerator<Object>> generators =
			arbitraries.stream()
				  .map(a -> a.generatorWithEmbeddedEdgeCases(genSize))
				  .collect(Collectors.toList());
		return random -> {
			List<Shrinkable<Object>> shrinkables = generateShrinkables(generators, random);
			return combineShrinkables(shrinkables, combineFunction);
		};
	}

	private List<Shrinkable<Object>> generateShrinkables(List<RandomGenerator<Object>> generators, JqwikRandom random) {
		List<Shrinkable<Object>> list = new ArrayList<>();
		for (RandomGenerator<Object> generator : generators) {
			list.add(generator.next(random));
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
