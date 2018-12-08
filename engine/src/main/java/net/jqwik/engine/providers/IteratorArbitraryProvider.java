package net.jqwik.engine.providers;

import java.util.*;

import net.jqwik.api.*;

public class IteratorArbitraryProvider extends AbstractCollectionArbitraryProvider {

	@Override
	protected Class<?> getProvidedType() {
		return Iterator.class;
	}

	@Override
	protected Arbitrary<?> create(Arbitrary<?> innerArbitrary) {
		return innerArbitrary.iterator();
	}
}
