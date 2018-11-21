package net.jqwik.properties.stateful;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.shrinking.*;

class ShrinkableActionSequence<M> implements Shrinkable<ActionSequence<M>> {

	private final ComprehensiveListShrinkingCandidates listShrinkingCandidates = new ComprehensiveListShrinkingCandidates();

	private final List<Shrinkable<Action<M>>> candidateActions;
	private final ActionSequence<M> value;

	ShrinkableActionSequence(List<Shrinkable<Action<M>>> candidateActions) {
		this.candidateActions = candidateActions;
		this.value = toActionSequence(extractValues(candidateActions));
	}

	private List<Action<M>> extractValues(List<Shrinkable<Action<M>>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	@Override
	public ShrinkingSequence<ActionSequence<M>> shrink(Falsifier<ActionSequence<M>> falsifier) {
		return shrinkSequenceOfActions(falsifier)
			.andThen(shrinkableList -> { //
				ShrinkableActionSequence<M> actionSequence = (ShrinkableActionSequence<M>) shrinkableList;
				Falsifier<List<Action<M>>> listFalsifier = list -> falsifier.test(toActionSequence(list));
				return shrinkIndividualActions(actionSequence, listFalsifier)
					// Shrink list of actions again since element shrinking
					// might have made some actions unnecessary
					.andThen(shrinkListOfActions(listFalsifier))
					.mapValue(this::toActionSequence);
			});

	}

	private DeepSearchShrinkingSequence<ActionSequence<M>> shrinkSequenceOfActions(Falsifier<ActionSequence<M>> falsifier) {
		return new DeepSearchShrinkingSequence<>(this, this::shrinkSequenceCandidates, falsifier);
	}

	private Function<Shrinkable<List<Action<M>>>, ShrinkingSequence<List<Action<M>>>> shrinkListOfActions(Falsifier<List<Action<M>>> listFalsifier) {
		return shrinkableListOfActions ->
			new DeepSearchShrinkingSequence<>(shrinkableListOfActions, this::shrinkActionListCandidates, listFalsifier);
	}

	private Set<Shrinkable<List<Action<M>>>> shrinkActionListCandidates(Shrinkable<List<Action<M>>> shrinkableList) {
		//noinspection unchecked
		return listShrinkingCandidates
			.candidatesFor(shrinkableList.value())
			.stream() //
			.map(elements -> elements.stream().map(Shrinkable::unshrinkable).collect(Collectors.toList())) //
			.map((List<Shrinkable<Action<M>>> shrinkableElements) -> (Shrinkable<List<Action<M>>>) new ShrinkableList(shrinkableElements, 1)) //
			.collect(Collectors.toSet());
	}

	private ElementsShrinkingSequence<Action<M>> shrinkIndividualActions(
		ShrinkableActionSequence<M> sequence,
		Falsifier<List<Action<M>>> listFalsifier
	) {
		return new ElementsShrinkingSequence<>(sequence.candidateActions, listFalsifier, ShrinkingDistance::forCollection);
	}

	private SequentialActionSequence<M> toActionSequence(List<Action<M>> list) {
		return new SequentialActionSequence<>(list);
	}

	private Set<Shrinkable<ActionSequence<M>>> shrinkSequenceCandidates(Shrinkable<ActionSequence<M>> shrinkable) {
		ShrinkableActionSequence<M> shrinkableSequence = (ShrinkableActionSequence<M>) shrinkable;
		//noinspection Convert2MethodRef
		return listShrinkingCandidates
			.candidatesFor(shrinkableSequence.candidateActions) //
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
