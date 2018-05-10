package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

class ShrinkableActionSequence<M> implements Shrinkable<ActionSequence<M>> {

	private final ActionSequenceShrinkCandidates<M> sequenceShrinker = new ActionSequenceShrinkCandidates<>();
	private final List<Shrinkable<Action<M>>> candidateActions;
	private final SequentialActionSequence<M> value;

	ShrinkableActionSequence(List<Shrinkable<Action<M>>> candidateActions) {
		this.candidateActions = candidateActions;
		this.value = new SequentialActionSequence<>(candidateActions);
	}

	@Override
	public Set<ShrinkResult<Shrinkable<ActionSequence<M>>>> shrinkNext(Predicate<ActionSequence<M>> falsifier) {
		Set<List<Shrinkable<Action<M>>>> shrinkingCandidates = sequenceShrinker.nextCandidates(candidateActions);
		Set<ShrinkResult<Shrinkable<ActionSequence<M>>>> shrunkList = shrinkSequence(falsifier, shrinkingCandidates);
		if (shrunkList.isEmpty()) {
			return shrinkActions(falsifier);
		}
		return shrunkList;
	}

	private Set<ShrinkResult<Shrinkable<ActionSequence<M>>>> shrinkSequence(
		Predicate<ActionSequence<M>> falsifier, Set<List<Shrinkable<Action<M>>>> candidates
	) {
		return candidates
			.stream() //
			.map(shrunkValue -> SafeFalsifier.falsify(falsifier, new ShrinkableActionSequence<>(shrunkValue))) //
			.filter(Optional::isPresent) //
			.map(Optional::get) //
			.collect(Collectors.toSet());
	}

	private Set<ShrinkResult<Shrinkable<ActionSequence<M>>>> shrinkActions(Predicate<ActionSequence<M>> falsifier) {
		Predicate<List<Action<M>>> valuesFalsifier = list -> {
			List<Shrinkable<Action<M>>> listShrinkable = list.stream().map(Shrinkable::unshrinkable).collect(Collectors.toList());
			ActionSequence<M> actionSequence = new SequentialActionSequence<>(listShrinkable);
			return falsifier.test(actionSequence);
		};
		ParameterListShrinker<Action<M>> listElementShrinker = new ParameterListShrinker<>(candidateActions, e -> {}, new Reporting[0], ShrinkingMode.FULL);
		Set<ShrinkResult<List<Shrinkable<Action<M>>>>> shrunkElements = listElementShrinker.shrinkNext(valuesFalsifier);
		return shrunkElements //
			.stream() //
			.map(listShrinkResult -> listShrinkResult //
				.map(listShrinkableAction -> (Shrinkable<ActionSequence<M>>) new ShrinkableActionSequence<>(listShrinkableAction)))
			.collect(Collectors.toSet());
	}

	@Override
	public ActionSequence<M> value() {
		return value;
	}

	@Override
	public int distance() {
		return sequenceShrinker.distance(candidateActions);
	}
}
