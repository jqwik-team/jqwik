package net.jqwik.newArbitraries;

import java.util.*;

public interface NShrinker<T> {

	Set<T> nextShrinkingCandidates(T value);

	default int distance(T value) {
		return 0;
	}
}
