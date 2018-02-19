package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

public interface SizableArbitrary<U> extends Arbitrary<U> {
	SizableArbitrary<U> withMinSize(int minSize);

	SizableArbitrary<U> withMaxSize(int maxSize);
}
