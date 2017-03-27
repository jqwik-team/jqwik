package net.jqwik.execution.properties.providers;

import java.util.*;
import java.util.stream.*;

import javaslang.test.*;
import net.jqwik.api.properties.*;

public class StreamArbitraryProvider extends AbstractCollectionArbitraryProvider {

	@Override
	protected Class<?> getProvidedType() {
		return Stream.class;
	}

	@Override
	protected Arbitrary<?> create(Arbitrary<?> innerArbitrary) {
		return Generator.streamOf(innerArbitrary);
	}
}
