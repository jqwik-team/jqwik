package net.jqwik.execution.properties.providers;

import javaslang.test.*;
import net.jqwik.api.properties.*;

import java.util.*;

public class SetArbitraryProvider extends AbstractCollectionArbitraryProvider {

	@Override
	protected Class<?> getProvidedType() {
		return Set.class;
	}

	@Override
	protected Arbitrary<?> create(Arbitrary<?> innerArbitrary) {
		return Generator.set(innerArbitrary);
	}
}
