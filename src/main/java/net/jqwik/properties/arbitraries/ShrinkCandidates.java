package net.jqwik.properties.arbitraries;

import java.util.*;

public interface ShrinkCandidates<T> {

	Set<T> nextCandidates(T value);

	default int distance(T value) {
		return 0;
	}
}
