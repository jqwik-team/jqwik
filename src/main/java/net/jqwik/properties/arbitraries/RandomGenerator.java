package net.jqwik.properties.arbitraries;

import java.util.*;

public interface RandomGenerator<T> {

	T next(Random random);
}
