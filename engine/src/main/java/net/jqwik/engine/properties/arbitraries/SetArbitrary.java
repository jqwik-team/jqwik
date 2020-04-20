package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

public class SetArbitrary<T> extends DefaultCollectionArbitrary<T, Set<T>> {

	public SetArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary, true);
	}

	@Override
	protected Iterable<T> toIterable(Set<T> streamable) {
		return streamable;
	}

	@Override
	public RandomGenerator<Set<T>> generator(int genSize) {
		int cutoffSize = cutoffSize(genSize);
		RandomGenerator<T> elementGenerator = elementGenerator(elementArbitrary, genSize);
		return RandomGenerators.set(elementGenerator, minSize, maxSize, cutoffSize).withEdgeCases(genSize, edgeCases());
	}

	@Override
	public Optional<ExhaustiveGenerator<Set<T>>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.set(elementArbitrary, minSize, maxSize, maxNumberOfSamples);
	}

	@Override
	public EdgeCases<Set<T>> edgeCases() {
		return edgeCases((elementList, minSize) -> {
			Set<Shrinkable<T>> elementSet = new HashSet<>(elementList);
			return new ShrinkableSet<>(elementSet, minSize);
		});
	}

}
