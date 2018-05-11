package net.jqwik.properties.shrinking;

import java.util.*;

public class ComprehensiveListShrinkingCandidates<T> implements ShrinkingCandidates<List<T>> {

	@Override
	public Set<List<T>> candidatesFor(List<T> toShrink) {
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
