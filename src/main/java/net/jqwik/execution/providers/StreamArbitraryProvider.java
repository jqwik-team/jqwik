package net.jqwik.execution.providers;

import java.util.stream.Stream;

import net.jqwik.api.*;

public class StreamArbitraryProvider extends AbstractCollectionArbitraryProvider {

	@Override
	protected Class<?> getProvidedType() {
		return Stream.class;
	}

	@Override
	protected Arbitrary<?> create(Arbitrary<?> innerArbitrary) {
		return Arbitraries.streamOf(innerArbitrary);
	}
}
