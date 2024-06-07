package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

import org.jspecify.annotations.*;

public class CombineArbitrary<R extends @Nullable Object> implements Arbitrary<R> {

	private final Function<? super List<?>, ? extends R> combinator;
	private final List<Arbitrary<Object>> arbitraries;

	@SuppressWarnings("unchecked")
	public CombineArbitrary(Function<? super List<?>, ? extends R> combinator, Arbitrary<?>... arbitraries) {
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
		Function<? super List<?>, ? extends R> combineFunction,
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
		Function<? super List<?>, ? extends R> combineFunction,
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

	private List<Shrinkable<Object>> generateShrinkables(List<RandomGenerator<Object>> generators, Random random) {
		List<Shrinkable<Object>> list = new ArrayList<>();
		for (RandomGenerator<Object> generator : generators) {
			list.add(generator.next(random));
		}
		return list;
	}

	private Shrinkable<R> combineShrinkables(
		List<Shrinkable<Object>> shrinkables, Function<? super List<?>, ? extends R> combineFunction
	) {
		return new CombinedShrinkable<>(shrinkables, combineFunction);
	}

	private Optional<ExhaustiveGenerator<R>> combineExhaustive(
		List<Arbitrary<Object>> arbitraries,
		Function<? super List<?>, ? extends R> combineFunction,
		long maxNumberOfSamples
	) {
		return ExhaustiveGenerators.combine(arbitraries, combineFunction, maxNumberOfSamples);
	}

	private EdgeCases<R> combineEdgeCases(
		final List<Arbitrary<Object>> arbitraries,
		final Function<? super List<?>, ? extends R> combineFunction,
		int maxEdgeCases
	) {
		return EdgeCasesSupport.combine(arbitraries, combineFunction, maxEdgeCases);
	}

}
