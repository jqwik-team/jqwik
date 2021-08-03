package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

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
			final Function<List<Object>, R> combineFunction,
			int maxEdgeCases
	) {
		if (arbitraries.isEmpty() || maxEdgeCases <= 0) {
			return EdgeCases.none();
		}
		List<Iterable<Supplier<Shrinkable<Object>>>> listOfSuppliers =
			arbitraries.stream()
				.map(a -> a.edgeCases(maxEdgeCases).suppliers())
				.collect(Collectors.toList());

		Iterator<List<Supplier<Shrinkable<Object>>>> iterator = Combinatorics.combine(listOfSuppliers);

		List<Supplier<Shrinkable<R>>> suppliers = new ArrayList<>();
		int count = 0;
		while(iterator.hasNext() && count < maxEdgeCases) {
			List<Supplier<Shrinkable<Object>>> next = iterator.next();
			List<Shrinkable<Object>> shrinkables = next.stream().map(Supplier::get).collect(Collectors.toList());
			suppliers.add(() -> new CombinedShrinkable<>(shrinkables, combineFunction));
			count++;
		}

		return EdgeCases.fromSuppliers(suppliers);
	}
}
