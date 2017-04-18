package net.jqwik.execution.properties.providers;

import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

import java.util.stream.*;

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
