package net.jqwik.engine.properties.shrinking;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class StartWithShrinkingSequence<T> implements ShrinkingSequence<T> {
	private final Shrinkable<T> starter;
	private final Falsifier<T> falsifier;
	private FalsificationResult<T> current;
	private ShrinkingSequence<T> startersSequence;

	public StartWithShrinkingSequence(Shrinkable<T> starter, Falsifier<T> falsifier) {
		this.starter = starter;
		this.falsifier = falsifier;
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter) {
		if (startersSequence != null) {
			boolean next = startersSequence.next(count, falsifiedReporter);
			this.current = startersSequence.current();
			return next;
		}
		if (falsifier.executeTry(starter.value()).status() == TryExecutionResult.Status.SATISFIED) {
			return false;
		}
		startersSequence = starter.shrink(falsifier);
		this.current = this.current.map(ignore -> starter);
		return true;
	}

	@Override
	public FalsificationResult<T> current() {
		return current;
	}

	@Override
	public void init(FalsificationResult<T> initialCurrent) {
		this.current = initialCurrent;
	}
}
