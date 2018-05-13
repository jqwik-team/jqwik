package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.shrinking.*;

import java.util.*;
import java.util.stream.*;

class ShrinkableActionSequence<M> implements Shrinkable<ActionSequence<M>> {

	private final ComprehensiveListShrinkingCandidates<Shrinkable<Action<M>>> candidates = new ComprehensiveListShrinkingCandidates<>();
	private final List<Shrinkable<Action<M>>> candidateActions;
	private final SequentialActionSequence<M> value;

	ShrinkableActionSequence(List<Shrinkable<Action<M>>> candidateActions) {
		this.candidateActions = candidateActions;
		this.value = toActionSequence(extractValues(candidateActions));
	}

	private List<Action<M>> extractValues(List<Shrinkable<Action<M>>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	@Override
	public ShrinkingSequence<ActionSequence<M>> shrink(Falsifier<ActionSequence<M>> falsifier) {
		return new DeepSearchShrinkingSequence<>(this, this::shrinkCandidatesFor, falsifier) //
			.andThen(shrinkableList -> { //
				ShrinkableActionSequence<M> actionSequence = (ShrinkableActionSequence<M>) shrinkableList;
				Falsifier<List<Action<M>>> listFalsifier = list -> falsifier.test(toActionSequence(list));
				return new ElementsShrinkingSequence<>(actionSequence.candidateActions, null, listFalsifier, ShrinkingDistance::forCollection)
					.mapValue(this::toActionSequence);
			});

	}

	private SequentialActionSequence<M> toActionSequence(List<Action<M>> list) {
		return new SequentialActionSequence<>(list);
	}

	@SuppressWarnings("unchecked")
	private Set<Shrinkable<ActionSequence<M>>> shrinkCandidatesFor(Shrinkable<ActionSequence<M>> shrinkable) {
		ShrinkableActionSequence<M> shrinkableSequence = (ShrinkableActionSequence<M>) shrinkable;
		//noinspection Convert2MethodRef
		return candidates.candidatesFor(shrinkableSequence.candidateActions) //
						 .stream() //
						 .map(list -> new ShrinkableActionSequence<>(list)) //
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
