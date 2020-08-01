package net.jqwik.engine.properties.stateful;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.engine.support.*;

class ShrinkableActionSequence<T> implements Shrinkable<ActionSequence<T>> {

	private final ActionGenerator<T> actionGenerator;
	private final int minSize;
	private final int maxSize;
	private final ShrinkingDistance distance;

	private SequentialActionSequence<T> generatedSequence = null;

	ShrinkableActionSequence(ActionGenerator<T> actionGenerator, int minSize, int maxSize, ShrinkingDistance distance) {
		this.actionGenerator = actionGenerator;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.distance = distance;
	}

	@Override
	public ActionSequence<T> value() {
		if (generatedSequence == null) {
			generatedSequence = new SequentialActionSequence<>(actionGenerator, maxSize);
		}
		return generatedSequence;
	}

	@Override
	public Stream<Shrinkable<ActionSequence<T>>> shrink() {
		if (generatedSequence == null) {
			return Stream.empty();
		}
		return JqwikStreamSupport.concat(
			shrinkSequenceOfActions(),
			shrinkActionsOneAfterTheOther()
		);
	}

	private Stream<Shrinkable<ActionSequence<T>>> shrinkSequenceOfActions() {
		return new ComprehensiveSizeOfListShrinkingCandidates()
				   .candidatesFor(actionGenerator.generated())
				   .filter(listOfActions -> listOfActions.size() >= minSize)
				   .map(this::createShrinkableActionSequence);
	}

	private Stream<Shrinkable<ActionSequence<T>>> shrinkActionsOneAfterTheOther() {
		List<Shrinkable<Action<T>>> shrinkableActions = actionGenerator.generated();
		List<Stream<Shrinkable<ActionSequence<T>>>> shrinkPerElementStreams = new ArrayList<>();
		for (int i = 0; i < shrinkableActions.size(); i++) {
			int index = i;
			Shrinkable<Action<T>> element = shrinkableActions.get(i);
			Stream<Shrinkable<ActionSequence<T>>> shrinkElement = element.shrink().flatMap(shrunkElement -> {
				List<Shrinkable<Action<T>>> actionsCopy = new ArrayList<>(shrinkableActions);
				actionsCopy.set(index, shrunkElement);
				return Stream.of(createShrinkableActionSequence(actionsCopy));
			});
			shrinkPerElementStreams.add(shrinkElement);
		}
		return JqwikStreamSupport.concat(shrinkPerElementStreams);
	}

	private ShrinkableActionSequence<T> createShrinkableActionSequence(List<Shrinkable<Action<T>>> list) {
		ActionGenerator<T> newGenerator = new ShrinkablesActionGenerator<>(list);
		ShrinkingDistance newDistance = ShrinkingDistance.forCollection(list);
		return new ShrinkableActionSequence<>(newGenerator, minSize, list.size(), newDistance);
	}

	@Override
	public ShrinkingDistance distance() {
		if (generatedSequence == null) {
			return distance;
		}
		return ShrinkingDistance.forCollection(actionGenerator.generated());
	}

}
