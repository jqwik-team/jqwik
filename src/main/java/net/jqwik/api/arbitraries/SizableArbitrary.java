package net.jqwik.api.arbitraries;

public interface SizableArbitrary<U> extends NullableArbitrary<U> {
	SizableArbitrary<U> withMinSize(int minSize);

	SizableArbitrary<U> withMaxSize(int maxSize);
}
