package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

import static net.jqwik.engine.properties.arbitraries.ArbitrariesSupport.*;

/**
 * Is loaded through reflection in api module
 */
public class ArbitraryFacadeImpl extends Arbitrary.ArbitraryFacade {
	@Override
	public <T, U> Optional<ExhaustiveGenerator<U>> flatMapExhaustiveGenerator(
		ExhaustiveGenerator<T> self,
		Function<T, Arbitrary<U>> mapper,
		long maxNumberOfSamples
	) {
		return ExhaustiveGenerators.flatMap(self, mapper, maxNumberOfSamples);
	}

	@Override
	public <T> StreamableArbitrary<T, List<T>> list(Arbitrary<T> elementArbitrary) {
		return new ListArbitrary<>(elementArbitrary);
	}

	@Override
	public <T> StreamableArbitrary<T, List<T>> listOfUnique(Arbitrary<T> uniqueArbitrary) {
		return new ListArbitrary<>(uniqueArbitrary)
				   .ofMaxSize(maxNumberOfElements(uniqueArbitrary, RandomGenerators.DEFAULT_COLLECTION_SIZE));
	}

	@Override
	public <T> StreamableArbitrary<T, Set<T>> set(Arbitrary<T> elementArbitrary) {
		// The set cannot be larger than the max number of possible elements
		return new SetArbitrary<>(elementArbitrary)
				   .ofMaxSize(maxNumberOfElements(elementArbitrary, RandomGenerators.DEFAULT_COLLECTION_SIZE));
	}

	@Override
	public <T> StreamableArbitrary<T, Stream<T>> stream(Arbitrary<T> elementArbitrary) {
		return new StreamArbitrary<>(elementArbitrary);
	}

	@Override
	public <T> StreamableArbitrary<T, Iterator<T>> iterator(Arbitrary<T> elementArbitrary) {
		return new IteratorArbitrary<>(elementArbitrary);
	}

	@Override
	public <T, A> SizableArbitrary<A> array(Arbitrary<T> elementArbitrary, Class<A> arrayClass) {
		return new ArrayArbitrary<>(elementArbitrary, arrayClass);
	}

	@Override
	public <T, A> SizableArbitrary<A> arrayOfUnique(Arbitrary<T> uniqueArbitrary, Class<A> arrayClass) {
		return new ArrayArbitrary<>(uniqueArbitrary, arrayClass)
				   .ofMaxSize(maxNumberOfElements(uniqueArbitrary, RandomGenerators.DEFAULT_COLLECTION_SIZE));
	}
}
