package net.jqwik.newArbitraries;

import java.util.*;

public interface NShrinkableGenerator<T> {
	T next(Random random);

	Set<NShrunkValue<T>> shrink(T valueToShrink);
}
