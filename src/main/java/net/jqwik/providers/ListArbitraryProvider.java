package net.jqwik.providers;

import java.util.List;

import net.jqwik.api.*;

public class ListArbitraryProvider extends AbstractCollectionArbitraryProvider {

	@Override
	protected Class<?> getProvidedType() {
		return List.class;
	}

	@Override
	protected Arbitrary<?> create(Arbitrary<?> innerArbitrary) {
		return innerArbitrary.list();
	}
}
