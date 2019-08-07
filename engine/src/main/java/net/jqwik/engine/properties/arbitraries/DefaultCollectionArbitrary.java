package net.jqwik.engine.properties.arbitraries;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

abstract class DefaultCollectionArbitrary<T, U> extends MultivalueArbitraryBase<T> implements StreamableArbitrary<T, U> {

	DefaultCollectionArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary);
	}

	@Override
	public StreamableArbitrary<T, U> ofMinSize(int minSize) {
		DefaultCollectionArbitrary<T, U> clone = typedClone();
		clone.minSize = minSize;
		return clone;
	}

	@Override
	public StreamableArbitrary<T, U> ofMaxSize(int maxSize) {
		DefaultCollectionArbitrary<T, U> clone = typedClone();
		clone.maxSize = maxSize;
		return clone;
	}

	@Override
	public <R> Arbitrary<R> reduce(R initial, BiFunction<R, T, R> accumulator) {
		// TODO: Remove duplication with ArrayArbitrary.reduce
		return this.map(streamable -> {
			// Couldn't find a way to use Stream.reduce since it requires a combinator
			@SuppressWarnings("unchecked")
			R[] result = (R[]) new Object[]{initial};
			Iterable<T> iterable = toIterable(streamable);
			for (T each : iterable) {
				result[0] = accumulator.apply(result[0], each);
			}
			return result[0];
		});
	}

	protected abstract Iterable<T> toIterable(U streamable);
}
