package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

public class ComprehensiveSizeOfListShrinker {

	public <T> Stream<List<T>> shrink(List<T> toShrink, int minSize) {
		if (toShrink.size() <= minSize) {
			return Stream.empty();
		}
		Set<List<T>> setOfSequences = new HashSet<>();
		for (int i = 0; i < toShrink.size(); i++) {
			ArrayList<T> newCandidate = new ArrayList<>(toShrink);
			newCandidate.remove(i);
			setOfSequences.add(newCandidate);
		}
		return setOfSequences.stream();
	}
}
