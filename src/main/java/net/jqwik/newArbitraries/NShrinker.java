package net.jqwik.newArbitraries;

import java.util.*;

public interface NShrinker<T> {

	Set<T> shrink(T value);

	default int distance(T value) {
		return 0;
	}
}
