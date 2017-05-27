package net.jqwik.execution.providers;

import java.util.*;

import net.jqwik.properties.*;

public class SetArbitraryProvider extends AbstractCollectionArbitraryProvider {

	@Override
	protected Class<?> getProvidedType() {
		return Set.class;
	}

	@Override
	protected NArbitrary<?> create(NArbitrary<?> innerArbitrary) {
		return NArbitraries.setOf(innerArbitrary);
	}
}
