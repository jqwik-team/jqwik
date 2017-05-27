package net.jqwik.execution.providers;

import java.util.*;

import net.jqwik.properties.*;

public class ListArbitraryProvider extends AbstractCollectionArbitraryProvider {

	@Override
	protected Class<?> getProvidedType() {
		return List.class;
	}

	@Override
	protected NArbitrary<?> create(NArbitrary<?> innerArbitrary) {
		return NArbitraries.listOf(innerArbitrary);
	}
}
