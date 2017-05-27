package net.jqwik.execution.providers;

import java.util.stream.*;

import net.jqwik.newArbitraries.*;

public class StreamArbitraryProvider extends AbstractCollectionArbitraryProvider {

	@Override
	protected Class<?> getProvidedType() {
		return Stream.class;
	}

	@Override
	protected NArbitrary<?> create(NArbitrary<?> innerArbitrary) {
		return NArbitraries.streamOf(innerArbitrary);
	}
}
