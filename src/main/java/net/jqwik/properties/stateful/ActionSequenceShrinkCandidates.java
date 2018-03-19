package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;

class ActionSequenceShrinkCandidates<M> implements ShrinkCandidates<List<Shrinkable<Action<M>>>> {

	@Override
	public Set<List<Shrinkable<Action<M>>>> nextCandidates(List<Shrinkable<Action<M>>> value) {
		if (value.size() <= 1) {
			return Collections.emptySet();
		}
		Set<List<Shrinkable<Action<M>>>> setOfSequences = new HashSet<>();
		for (int i = 0; i < value.size(); i++) {
			ArrayList<Shrinkable<Action<M>>> newCandidate = new ArrayList<>(value);
			newCandidate.remove(i);
			setOfSequences.add(newCandidate);
		}
		return setOfSequences;
	}

	@Override
	public int distance(List<Shrinkable<Action<M>>> actions) {
		// The algorithm is more involved because the distance may never never never overflow
		// TODO: Remove duplication with ListShrinkCandidates.distance()
		int sumOfDistances = 0;
		for (Shrinkable<Action<M>> shrinkable : actions) {
			int distance = shrinkable.distance();
			long newDistance = (long) sumOfDistances + (long) distance;
			if (newDistance >= Integer.MAX_VALUE)
				return Integer.MAX_VALUE;
			sumOfDistances = (int) newDistance;
		}
		return actions.size() + sumOfDistances;
	}

}
