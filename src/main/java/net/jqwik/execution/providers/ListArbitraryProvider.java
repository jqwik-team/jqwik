package net.jqwik.execution.providers;

import net.jqwik.properties.*;

import java.util.*;

public class ListArbitraryProvider extends AbstractCollectionArbitraryProvider {

	@Override
	protected Class<?> getProvidedType() {
		return List.class;
	}

	@Override
	protected Arbitrary<?> create(Arbitrary<?> innerArbitrary) {
		return Arbitraries.listOf(innerArbitrary);
	}
}
