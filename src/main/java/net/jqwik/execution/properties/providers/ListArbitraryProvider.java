package net.jqwik.execution.properties.providers;

import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

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
