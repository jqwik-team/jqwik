package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;

/**
 * Is loaded through reflection in api module
 */
public class ArbitraryFacadeImpl extends Arbitrary.ArbitraryFacade {
	@Override
	public <T, U> Optional<ExhaustiveGenerator<U>> flatMapExhaustiveGenerator(
		ExhaustiveGenerator<T> self, Function<T, Arbitrary<U>> mapper
	) {
		return ExhaustiveGenerators.flatMap(self, mapper);
	}

	@Override
	public <T> SizableArbitrary<List<T>> list(Arbitrary<T> elementArbitrary) {
		return new ListArbitrary<>(elementArbitrary);
	}

	@Override
	public <T> SizableArbitrary<Set<T>> set(Arbitrary<T> elementArbitrary) {
		return new SetArbitrary<>(elementArbitrary);
	}

	@Override
	public <T> SizableArbitrary<Stream<T>> stream(Arbitrary<T> elementArbitrary) {
		return new StreamArbitrary<>(elementArbitrary);
	}

	@Override
	public <T> SizableArbitrary<Iterator<T>> iterator(Arbitrary<T> elementArbitrary) {
		return new IteratorArbitrary<>(elementArbitrary);
	}

	@Override
	public <T, A> SizableArbitrary<A> array(Arbitrary<T> elementArbitrary, Class<A> arrayClass) {
		return new ArrayArbitrary<>(elementArbitrary, arrayClass);
	}
}
