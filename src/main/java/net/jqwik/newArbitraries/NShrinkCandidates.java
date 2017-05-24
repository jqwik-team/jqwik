package net.jqwik.newArbitraries;

import java.util.*;

public interface NShrinkCandidates<T> {

	Set<T> nextCandidates(T value);

	default int distance(T value) {
		return 0;
	}
}
