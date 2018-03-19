package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

class ShrinkableActionSequence<M> implements Shrinkable<ActionSequence<M>> {

	private final ActionSequenceShrinkCandidates<M> sequenceShrinker = new ActionSequenceShrinkCandidates<>();
	private final ActionSequence<M> value;

	ShrinkableActionSequence(ActionSequence<M> value) {
		this.value = value;
	}

	@Override
	public Set<ShrinkResult<Shrinkable<ActionSequence<M>>>> shrinkNext(Predicate<ActionSequence<M>> falsifier) {
		Set<ActionSequence<M>> candidates = sequenceShrinker.nextCandidates(value);
		Set<ShrinkResult<Shrinkable<ActionSequence<M>>>> shrunkList = shrinkSequence(falsifier, candidates);
		if (shrunkList.isEmpty()) {
			return shrinkActions(falsifier);
		}
		return shrunkList;
	}

	private Set<ShrinkResult<Shrinkable<ActionSequence<M>>>> shrinkSequence(
		Predicate<ActionSequence<M>> falsifier, Set<ActionSequence<M>> candidates
	) {
		return candidates
			.stream() //
			.map(shrunkValue -> SafeFalsifier.falsify(falsifier, new ShrinkableActionSequence<>(shrunkValue))) //
			.filter(Optional::isPresent) //
			.map(Optional::get) //
			.collect(Collectors.toSet());
	}

	private Set<ShrinkResult<Shrinkable<ActionSequence<M>>>> shrinkActions(Predicate<ActionSequence<M>> falsifier) {
		return Collections.emptySet();
	}

	@Override
	public ActionSequence<M> value() {
		return value;
	}

	@Override
	public int distance() {
		return sequenceShrinker.distance(value);
	}
}
