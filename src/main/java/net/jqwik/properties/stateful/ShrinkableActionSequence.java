package net.jqwik.properties.stateful;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.shrinking.*;

public class ShrinkableActionSequence<T> implements Shrinkable<ActionSequence<T>> {

	private final ComprehensiveListShrinkingCandidates listShrinkingCandidates = new ComprehensiveListShrinkingCandidates();

	private final ActionSequence<T> value;
	private final ActionGenerator<T> actionGenerator;
	private final ShrinkingDistance distance;

	public ShrinkableActionSequence(ActionGenerator<T> actionGenerator, int size, ShrinkingDistance distance) {
		this.actionGenerator = actionGenerator;
		this.distance = distance;
		this.value = new SequentialActionSequence<>(actionGenerator, size);
	}

	@Override
	public ActionSequence<T> value() {
		return value;
	}

	@Override
	public ShrinkingSequence<ActionSequence<T>> shrink(Falsifier<ActionSequence<T>> falsifier) {
		return shrinkSequenceOfActions(falsifier)
			.andThen(shrinkableList -> { //
				ShrinkableActionSequence<T> shrinkableSequence = (ShrinkableActionSequence<T>) shrinkableList;
				Falsifier<List<Action<T>>> listFalsifier = list -> falsifier.test(toActionSequence(list));
				return shrinkIndividualActions(shrinkableSequence, listFalsifier)
					// Shrink list of actions again since element shrinking
					// might have made some actions unnecessary
					.andThen(shrinkListOfActions(listFalsifier))
					.mapValue(this::toActionSequence);
			});

	}

	private Function<Shrinkable<List<Action<T>>>, ShrinkingSequence<List<Action<T>>>> shrinkListOfActions(Falsifier<List<Action<T>>> listFalsifier) {
		return shrinkableListOfActions ->
			new DeepSearchShrinkingSequence<>(shrinkableListOfActions, this::shrinkActionListCandidates, listFalsifier);
	}

	private Set<Shrinkable<List<Action<T>>>> shrinkActionListCandidates(Shrinkable<List<Action<T>>> shrinkableList) {
		//noinspection unchecked
		return listShrinkingCandidates
			.candidatesFor(shrinkableList.value())
			.stream()
			.map(elements -> elements.stream().map(Shrinkable::unshrinkable).collect(Collectors.toList()))
			.map((List<Shrinkable<Action<T>>> shrinkableElements) -> (Shrinkable<List<Action<T>>>) new ShrinkableList(shrinkableElements, 1)) //
			.collect(Collectors.toSet());
	}

	private ElementsShrinkingSequence<Action<T>> shrinkIndividualActions(
		ShrinkableActionSequence<T> shrinkableActionSequence,
		Falsifier<List<Action<T>>> listFalsifier
	) {
		return new ElementsShrinkingSequence<>(
			shrinkableActionSequence.actionGenerator.generated(),
			listFalsifier, ShrinkingDistance::forCollection
		);
	}

	private ActionSequence<T> toActionSequence(List<Action<T>> listOfActions) {
		ActionGenerator<T> newActionGenerator = new ListActionGenerator<>(listOfActions);
		return new SequentialActionSequence<>(newActionGenerator, listOfActions.size());
	}

	private ShrinkableActionSequence<T> toShrinkableActionSequence(List<Shrinkable<Action<T>>> list) {
		ActionGenerator<T> newGenerator = new ShrinkablesActionGenerator<>(list);
		ShrinkingDistance newDistance = ShrinkingDistance.forCollection(list);
		return new ShrinkableActionSequence<>(newGenerator, list
			.size(), newDistance);
	}

	private DeepSearchShrinkingSequence<ActionSequence<T>> shrinkSequenceOfActions(Falsifier<ActionSequence<T>> falsifier) {
		return new DeepSearchShrinkingSequence<>(this, this::shrinkSequenceCandidates, falsifier);
	}

	private Set<Shrinkable<ActionSequence<T>>> shrinkSequenceCandidates(Shrinkable<ActionSequence<T>> shrinkable) {
		ShrinkableActionSequence<T> shrinkableSequence = (ShrinkableActionSequence<T>) shrinkable;
		return listShrinkingCandidates
			.candidatesFor(shrinkableSequence.actionGenerator.generated())
			.stream()
			.map(this::toShrinkableActionSequence)
			.collect(Collectors.toSet());
	}

	@Override
	public ShrinkingDistance distance() {
		if (value.runState() == ActionSequence.RunState.NOT_RUN) {
			return distance;
		}
		return ShrinkingDistance.forCollection(actionGenerator.generated());
	}

}
