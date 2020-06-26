package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.*;
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
		return new DefaultListArbitrary<>(elementArbitrary, elementArbitrary.isUnique());
	}

	@Override
	public <T> StreamableArbitrary<T, Set<T>> set(Arbitrary<T> elementArbitrary) {
		// The set cannot be larger than the max number of possible elements
		return new DefaultSetArbitrary<>(elementArbitrary)
				   .ofMaxSize(maxNumberOfElements(elementArbitrary, RandomGenerators.DEFAULT_COLLECTION_SIZE));
	}

	@Override
	public <T> StreamableArbitrary<T, Stream<T>> stream(Arbitrary<T> elementArbitrary) {
		return new DefaultStreamArbitrary<>(elementArbitrary, elementArbitrary.isUnique());
	}

	@Override
	public <T> StreamableArbitrary<T, Iterator<T>> iterator(Arbitrary<T> elementArbitrary) {
		return new DefaultIteratorArbitrary<>(elementArbitrary, elementArbitrary.isUnique());
	}

	@Override
	public <T, A> StreamableArbitrary<T, A> array(Arbitrary<T> elementArbitrary, Class<A> arrayClass) {
		return new DefaultArrayArbitrary<>(elementArbitrary, arrayClass, elementArbitrary.isUnique());
	}

	@Override
	public <T> Stream<T> sampleStream(Arbitrary<T> arbitrary) {
		return arbitrary.generator(JqwikProperties.DEFAULT_TRIES)
						.stream(SourceOfRandomness.current())
						.map(Shrinkable::value);
	}
}
