package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.shrinking.*;

public class ListArbitrary<T> extends DefaultCollectionArbitrary<T, List<T>> {

	public ListArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary);
	}

	@Override
	protected Iterable<T> toIterable(List<T> streamable) {
		return streamable;
	}

	@Override
	public RandomGenerator<List<T>> generator(int genSize) {
		return createListGenerator(genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<List<T>>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.list(elementArbitrary, minSize, maxSize, maxNumberOfSamples);
	}

//	@Override
//	public EdgeCases<List<T>> edgeCases() {
//		Set<Shrinkable<List<T>>> set = new HashSet<>();
//		set.add(new ShrinkableList<>(new ArrayList<>(), minSize));
//		Stream<Shrinkable<List<T>>> stream =
//			elementArbitrary.edgeCases()
//							.mapShrinkable((Shrinkable<T> shrinkableT) -> {
//								List<Shrinkable<T>> elements = Collections.singletonList(shrinkableT);
//								return new ShrinkableList<>(elements, minSize);
//							});
//		return EdgeCases.fromStream(stream);
//	}
}
