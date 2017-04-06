package net.jqwik.execution.properties.providers;

import java.util.*;

import javaslang.test.*;
import net.jqwik.api.properties.*;

public class ListArbitraryProvider extends AbstractCollectionArbitraryProvider {

	@Override
	protected Class<?> getProvidedType() {
		return List.class;
	}

	@Override
	protected Arbitrary<?> create(Arbitrary<?> innerArbitrary) {
		return Generator.listOf(innerArbitrary);
	}
}
