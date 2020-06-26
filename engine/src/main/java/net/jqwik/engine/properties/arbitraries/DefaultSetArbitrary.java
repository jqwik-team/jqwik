package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

public class DefaultSetArbitrary<T> extends MultivalueArbitraryBase<T, Set<T>> implements SetArbitrary<T> {

	public DefaultSetArbitrary(Arbitrary<T> elementArbitrary) {
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

	@Override
	public SetArbitrary<T> ofMaxSize(int maxSize) {
		return (SetArbitrary<T>) super.ofMaxSize(maxSize);
	}

	@Override
	public SetArbitrary<T> ofMinSize(int minSize) {
		return (SetArbitrary<T>) super.ofMinSize(minSize);
	}

	@Override
	public SetArbitrary<T> ofSize(int size) {
		return this.ofMinSize(size).ofMaxSize(size);
	}

	// TODO: Remove duplication with DefaultListArbitrary.mapEach()
	@Override
	public <U> Arbitrary<Set<U>> mapEach(BiFunction<Set<T>, T, U> mapper) {
		return this.map(elements -> elements.stream()
											.map(e -> mapper.apply(elements, e))
											.collect(Collectors.toSet()));
	}

	// TODO: Remove duplication with DefaultListArbitrary.flatMapEach()
	@Override
	public <U> Arbitrary<Set<U>> flatMapEach(BiFunction<Set<T>, T, Arbitrary<U>> flatMapper) {
		return this.flatMap(elements -> {
			List<Arbitrary<U>> arbitraries =
				elements.stream()
						.map(e -> flatMapper.apply(elements, e))
						.collect(Collectors.toList());
			return Combinators.combine(arbitraries).as(HashSet::new);
		});
	}
}
