package net.jqwik.properties.newShrinking;

import net.jqwik.api.stateful.*;

import java.util.*;
import java.util.stream.*;

class NShrinkableActionSequence<M> implements NShrinkable<ActionSequence<M>> {

	private final ComprehensiveListShrinkingCandidates<NShrinkable<Action<M>>> candidates = new ComprehensiveListShrinkingCandidates<>();
	private final List<NShrinkable<Action<M>>> candidateActions;
	private final NSequentialActionSequence<M> value;

	NShrinkableActionSequence(List<NShrinkable<Action<M>>> candidateActions) {
		this.candidateActions = candidateActions;
		this.value = toActionSequence(extractValues(candidateActions));
	}

	private List<Action<M>> extractValues(List<NShrinkable<Action<M>>> shrinkables) {
		return shrinkables.stream().map(NShrinkable::value).collect(Collectors.toList());
	}

	@Override
	public ShrinkingSequence<ActionSequence<M>> shrink(Falsifier<ActionSequence<M>> falsifier) {
		return new DeepSearchShrinkingSequence<>(this, this::shrinkCandidatesFor, falsifier) //
			.andThen(shrinkableList -> { //
				NShrinkableActionSequence<M> actionSequence = (NShrinkableActionSequence<M>) shrinkableList;
				Falsifier<List<Action<M>>> listFalsifier = list -> falsifier.test(toActionSequence(list));
				return new ElementsShrinkingSequence<Action<M>>(actionSequence.candidateActions, null, listFalsifier, ShrinkingDistance::forCollection)
					.map(this::toActionSequence);
			});

	}

	private NSequentialActionSequence<M> toActionSequence(List<Action<M>> list) {
		return new NSequentialActionSequence<>(list);
	}

	@SuppressWarnings("unchecked")
	private Set<NShrinkable<ActionSequence<M>>> shrinkCandidatesFor(NShrinkable<ActionSequence<M>> shrinkable) {
		NShrinkableActionSequence<M> shrinkableSequence = (NShrinkableActionSequence<M>) shrinkable;
		return candidates.candidatesFor(shrinkableSequence.candidateActions) //
						 .stream() //
						 .map(list -> new NShrinkableActionSequence<>(list)) //
						 .collect(Collectors.toSet());
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.forCollection(candidateActions);
	}

	@Override
	public ActionSequence<M> value() {
		return value;
	}

}
