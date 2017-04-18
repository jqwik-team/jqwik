package net.jqwik.execution.properties.providers;

import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;

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
