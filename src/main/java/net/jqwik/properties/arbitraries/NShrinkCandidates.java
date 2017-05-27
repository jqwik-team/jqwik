package net.jqwik.properties.arbitraries;

import java.util.*;

public interface NShrinkCandidates<T> {

	Set<T> nextCandidates(T value);

	default int distance(T value) {
		return 0;
	}
}
