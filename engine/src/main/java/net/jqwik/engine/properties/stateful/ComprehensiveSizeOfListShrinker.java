package net.jqwik.engine.properties.stateful;

import java.util.*;
import java.util.stream.*;

class ComprehensiveSizeOfListShrinker {

	<T> Stream<List<T>> shrink(List<T> toShrink, int minSize) {
		if (toShrink.size() <= minSize) {
			return Stream.empty();
		}
		Set<List<T>> setOfSequences = new LinkedHashSet<>();
		for (int i = 0; i < toShrink.size(); i++) {
			ArrayList<T> newCandidate = new ArrayList<>(toShrink);
			newCandidate.remove(i);
			setOfSequences.add(newCandidate);
		}
		return setOfSequences.stream();
	}
}
