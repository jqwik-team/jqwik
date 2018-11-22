package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

public class NShrinkableActionSequence<T> implements Shrinkable<ActionSequence<T>> {

	private final ActionSequence<T> value;
	private final ShrinkingDistance distance;

	public NShrinkableActionSequence(NActionGenerator<T> actionGenerator, int size, ShrinkingDistance distance) {
		this.distance = distance;
		this.value = new NSequentialActionSequence<>(actionGenerator, size);
	}

	@Override
	public ActionSequence<T> value() {
		return value;
	}

	@Override
	public ShrinkingSequence<ActionSequence<T>> shrink(Falsifier<ActionSequence<T>> falsifier) {
		return null;
	}

	@Override
	public ShrinkingDistance distance() {
		return distance;
	}

}
