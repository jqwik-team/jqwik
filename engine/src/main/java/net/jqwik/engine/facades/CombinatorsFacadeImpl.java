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
		final List<EdgeCases<Object>> listOfEdgeCases,
		final Function<List<Object>, R> combineFunction
	) {
		// TODO: This should also be possible with a stream().reduce() over listOfEdgeCases
		EdgeCases<List<Object>>[] combinedEdgeCases =
			new EdgeCases[]{EdgeCases.fromSupplier(() -> Shrinkable.unshrinkable(new ArrayList<>()))};
		for (EdgeCases<Object> current : listOfEdgeCases) {
			combinedEdgeCases[0] = current.flatMap(value -> combinedEdgeCases[0].map(list -> {
				list.add(value);
				return list;
			}));
		}
		return combinedEdgeCases[0].map(combineFunction);
	}
}
