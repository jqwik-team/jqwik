package net.jqwik.api.arbitraries;

public interface SizableArbitrary<U> extends NullableArbitrary<U> {
	default SizableArbitrary<U> ofSize(int size) {
		return ofMinSize(size).ofMaxSize(size);
	}

	SizableArbitrary<U> ofMinSize(int minSize);

	SizableArbitrary<U> ofMaxSize(int maxSize);
}
