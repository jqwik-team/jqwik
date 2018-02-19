package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

public interface CollectionArbitrary<U> extends Arbitrary<U> {
	CollectionArbitrary<U> withMinSize(int minSize);

	CollectionArbitrary<U> withMaxSize(int maxSize);
}
