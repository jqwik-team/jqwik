package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.stream.*;

public class ActionSequenceShrinkCandidates<M> implements ShrinkCandidates<ActionSequence<M>> {

	@Override
	public Set<ActionSequence<M>> nextCandidates(ActionSequence<M> value) {
		Set<List<Shrinkable<Action<M>>>> setOfSequences = shrinkActions(((SequentialActionSequence<M>) value).getRunSequence());
		return setOfSequences //
			.stream() //
			.map(SequentialActionSequence::new) //
			.collect(Collectors.toSet());
	}

	private Set<List<Shrinkable<Action<M>>>> shrinkActions(List<Shrinkable<Action<M>>> sequence) {
		if (sequence.size() <= 1) {
			return Collections.emptySet();
		}
		Set<List<Shrinkable<Action<M>>>> setOfSequences = new HashSet<>();
		for (int i = 0; i < sequence.size(); i++) {
			ArrayList<Shrinkable<Action<M>>> newCandidate = new ArrayList<>(sequence);
			newCandidate.remove(i);
			setOfSequences.add(newCandidate);
		}
		return setOfSequences;
	}

	@Override
	public int distance(ActionSequence<M> sequence) {
		return ((SequentialActionSequence<M>) sequence).getRunSequence().stream().mapToInt(Shrinkable::distance).sum();
	}
}
