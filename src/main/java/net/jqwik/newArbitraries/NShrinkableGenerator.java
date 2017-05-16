package net.jqwik.newArbitraries;

import java.util.*;

public interface NShrinkableGenerator<T> {
	NShrinkable<T> next(Random random);
}
