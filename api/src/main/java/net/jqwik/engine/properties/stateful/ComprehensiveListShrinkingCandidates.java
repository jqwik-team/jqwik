package net.jqwik.engine.properties.stateful;

import java.util.*;

/**
 * It no longer implements {@code ShrinkingCandidates<List<T>>} because its single use
 * in {@linkplain ShrinkableActionSequence} requires two different type signatures
 */
class ComprehensiveListShrinkingCandidates {

	public <T> Set<List<T>> candidatesFor(List<T> toShrink) {
		//At least one element will be kept
		if (toShrink.size() <= 1) {
			return Collections.emptySet();
		}
		Set<List<T>> setOfSequences = new HashSet<>();
		for (int i = 0; i < toShrink.size(); i++) {
			ArrayList<T> newCandidate = new ArrayList<>(toShrink);
			newCandidate.remove(i);
			setOfSequences.add(newCandidate);
		}
		return setOfSequences;
	}
}
