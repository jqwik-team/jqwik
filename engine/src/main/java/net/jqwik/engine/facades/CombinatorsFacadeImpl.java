package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

/**
 * Is loaded through reflection in api module
 */
public class CombinatorsFacadeImpl extends Combinators.CombinatorsFacade {

	@Override
	public <R> Arbitrary<R> combine(Function<List<Object>, R> combinator, Arbitrary<?>... arbitraries) {
		// This is a shorter implementation of as, which however would have worse shrinking
		// behaviour because it builds on flatMap:
		//		public <R> Arbitrary<R> as(F2<T1, T2, R> combinator) {
		//			return a1.flatMap(v1 -> a2.map(v2 -> combinator.apply(v1, v2)));
		//		}
		return new Arbitrary<R>() {
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
					asTypedList(arbitraries),
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
					asTypedList(arbitraries),
					combinator,
					maxEdgeCases
				);
			}

		};
	}

	@SuppressWarnings("unchecked")
	private static <T> List<T> asTypedList(Object... objects) {
		List<T> list = new ArrayList<>();
		for (Object object : objects) {
			list.add((T) object);
		}
		return list;
	}

	private static boolean isCombinedGeneratorMemoizable(Arbitrary<?>... arbitraries) {
		return Arrays.stream(arbitraries).allMatch(Arbitrary::isGeneratorMemoizable);
	}

	private static <T> RandomGenerator<T> combineGenerator(
		int genSize,
		Function<List<Object>, T> combineFunction,
		Arbitrary<?>... arbitraries
	) {
		List<RandomGenerator<?>> generators = Arrays.stream(arbitraries)
													.map(a -> a.generator(genSize))
													.collect(Collectors.toList());
		return random -> {
			List<Shrinkable<Object>> shrinkables = generateShrinkables(generators, random);
			return combineShrinkables(shrinkables, combineFunction);
		};
	}

	private static <T> RandomGenerator<T> combineGeneratorWithEmbeddedEdgeCases(
		int genSize,
		Function<List<Object>, @NotNull T> combineFunction,
		Arbitrary<?>... arbitraries
	) {
		List<RandomGenerator<?>> generators = Arrays.stream(arbitraries)
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

	private static <R> Shrinkable<R> combineShrinkables(
		List<Shrinkable<Object>> shrinkables, Function<List<Object>, R> combineFunction
	) {
		return new CombinedShrinkable<>(shrinkables, combineFunction);
	}

	private static <R> Optional<ExhaustiveGenerator<R>> combineExhaustive(
		List<Arbitrary<Object>> arbitraries,
		Function<List<Object>, R> combineFunction,
		long maxNumberOfSamples
	) {
		return ExhaustiveGenerators.combine(arbitraries, combineFunction, maxNumberOfSamples);
	}

	private static <R> EdgeCases<R> combineEdgeCases(
		final List<Arbitrary<Object>> arbitraries,
		final Function<List<Object>, R> combineFunction,
		int maxEdgeCases
	) {
		return EdgeCasesSupport.combine(arbitraries, combineFunction, maxEdgeCases);
	}

}
