package net.jqwik.engine.properties.stateful;

import java.util.*;
import java.util.stream.*;

class ComprehensiveSizeOfListShrinkingCandidates {

	public <T> Stream<List<T>> candidatesFor(List<T> toShrink) {
		//At least one element will be kept
		if (toShrink.size() <= 1) {
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
