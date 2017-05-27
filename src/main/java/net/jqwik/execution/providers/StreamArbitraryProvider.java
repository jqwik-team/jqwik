package net.jqwik.execution.providers;

import java.util.stream.*;

import net.jqwik.properties.*;

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
