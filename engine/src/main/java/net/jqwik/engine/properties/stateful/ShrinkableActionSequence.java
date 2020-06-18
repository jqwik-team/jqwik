package net.jqwik.engine.properties.stateful;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.engine.properties.shrinking.*;

class ShrinkableActionSequence<T> implements Shrinkable<ActionSequence<T>> {

	private final ComprehensiveListShrinkingCandidates listShrinkingCandidates = new ComprehensiveListShrinkingCandidates();

	private final ActionSequence<T> value;
	private final ActionGenerator<T> actionGenerator;
	private final int minSize;
	private final ShrinkingDistance distance;

	ShrinkableActionSequence(ActionGenerator<T> actionGenerator, int minSize, int maxSize, ShrinkingDistance distance) {
		this.actionGenerator = actionGenerator;
		this.minSize = minSize;
		this.distance = distance;
		this.value = new SequentialActionSequence<>(actionGenerator, maxSize);
	}

	@Override
	public ActionSequence<T> value() {
		return value;
	}

	@Override
	public ShrinkingSequence<ActionSequence<T>> shrink(Falsifier<ActionSequence<T>> falsifier) {
		Falsifier<ActionSequence<T>> minRespectingFalsifier =
			falsifier.withPostFilter(actionSequence -> actionSequence.runActions().size() >= minSize);

		return shrinkSequenceOfActions(minRespectingFalsifier)
				   .andThen(shrinkableList -> { //
					   ShrinkableActionSequence<T> shrinkableSequence = (ShrinkableActionSequence<T>) shrinkableList;
					   Falsifier<List<Action<T>>> listFalsifier = minRespectingFalsifier.map(this::toRunnableActionSequence);
					   return shrinkIndividualActions(shrinkableSequence, listFalsifier)
								  // Shrink list of actions again since element shrinking
								  // might have made some actions unnecessary
								  .andThen(shrinkListOfActions(listFalsifier))
								  .mapValue(this::toDisplayOnlyActionSequence);
				   });

	}

	private Function<Shrinkable<List<Action<T>>>, ShrinkingSequence<List<Action<T>>>> shrinkListOfActions(Falsifier<List<Action<T>>> listFalsifier) {
		return shrinkableListOfActions ->
				   new DeepSearchShrinkingSequence<>(shrinkableListOfActions, this::shrinkActionListCandidates, listFalsifier);
	}

	private Set<Shrinkable<List<Action<T>>>> shrinkActionListCandidates(Shrinkable<List<Action<T>>> shrinkableList) {
		return listShrinkingCandidates
				   .candidatesFor(shrinkableList.value())
				   .stream()
				   .map(elements -> elements.stream().map(Shrinkable::unshrinkable).collect(Collectors.toList()))
				   .map((List<Shrinkable<Action<T>>> shrinkableElements) -> (Shrinkable<List<Action<T>>>) new ShrinkableList(shrinkableElements, 1))
				   .collect(Collectors.toSet());
	}

	private ShrinkingSequence<List<Action<T>>> shrinkIndividualActions(
		ShrinkableActionSequence<T> shrinkableActionSequence,
		Falsifier<List<Action<T>>> listFalsifier
	) {
		return new ShrinkElementsSequence<>(
			shrinkableActionSequence.actionGenerator.generated(),
			listFalsifier,
			ShrinkingDistance::forCollection
		);
	}

	private ActionSequence<T> toDisplayOnlyActionSequence(List<Action<T>> listOfActions) {
		return new FixedActionsFailedActionSequence<>(listOfActions);
	}

	private ActionSequence<T> toRunnableActionSequence(List<Action<T>> listOfActions) {
		ActionGenerator<T> newActionGenerator = new ListActionGenerator<>(listOfActions);
		return new SequentialActionSequence<T>(newActionGenerator, listOfActions.size());
	}

	private ShrinkableActionSequence<T> toShrinkableActionSequence(List<Shrinkable<Action<T>>> list) {
		ActionGenerator<T> newGenerator = new ShrinkablesActionGenerator<>(list);
		ShrinkingDistance newDistance = ShrinkingDistance.forCollection(list);
		return new ShrinkableActionSequence<>(newGenerator, 1, list.size(), newDistance);
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
