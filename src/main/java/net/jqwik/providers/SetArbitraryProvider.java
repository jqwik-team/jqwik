package net.jqwik.providers;

import java.util.Set;

import net.jqwik.api.*;

public class SetArbitraryProvider extends AbstractCollectionArbitraryProvider {

	@Override
	protected Class<?> getProvidedType() {
		return Set.class;
	}

	@Override
	protected Arbitrary<?> create(Arbitrary<?> innerArbitrary) {
		return Arbitraries.setOf(innerArbitrary);
	}
}
