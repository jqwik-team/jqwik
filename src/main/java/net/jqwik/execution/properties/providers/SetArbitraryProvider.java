package net.jqwik.execution.properties.providers;

import java.util.*;

import javaslang.test.*;
import net.jqwik.api.properties.*;

public class SetArbitraryProvider extends AbstractCollectionArbitraryProvider {

	@Override
	protected Class<?> getProvidedType() {
		return Set.class;
	}

	@Override
	protected Arbitrary<?> create(Arbitrary<?> innerArbitrary) {
		return Generator.setOf(innerArbitrary);
	}
}
