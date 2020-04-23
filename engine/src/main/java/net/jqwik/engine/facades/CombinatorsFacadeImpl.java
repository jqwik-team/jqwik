package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

/**
 * Is loaded through reflection in api module
 */
public class CombinatorsFacadeImpl extends Combinators.CombinatorsFacade {
	@Override
	public <R> Shrinkable<R> combineShrinkables(
		List<Shrinkable<Object>> shrinkables, Function<List<Object>, R> combineFunction
	) {
		return new CombinedShrinkable<>(shrinkables, combineFunction);
	}

	@Override
	public <R> Optional<ExhaustiveGenerator<R>> combineExhaustive(
		List<Arbitrary<Object>> arbitraries,
		Function<List<Object>, R> combineFunction,
		long maxNumberOfSamples
	) {
		return ExhaustiveGenerators.combine(arbitraries, combineFunction, maxNumberOfSamples);
	}

	@Override
	public <R> EdgeCases<R> combineEdgeCases(
		final List<Arbitrary<Object>> arbitraries,
		final Function<List<Object>, R> combineFunction
	) {
		// TODO: This should also be possible with a stream().reduce() over listOfEdgeCases
		Arbitrary<List<Object>>[] combinedArbitrary = new Arbitrary[1];
		for (int i = 0; i < arbitraries.size(); i++) {
			Arbitrary<Object> current = arbitraries.get(i);
			if (i == 0) {
				combinedArbitrary[0] = current.map(Collections::singletonList);
			} else {
				combinedArbitrary[0] = combinedArbitrary[0].flatMap(list -> current.map(value -> {
					ArrayList<Object> result = new ArrayList<>(list);
					result.add(value);
					return result;
				}));
			}
		}
		return combinedArbitrary[0].map(combineFunction).edgeCases();
	}
}
