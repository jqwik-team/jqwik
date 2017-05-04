package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

public class ShrinkableSequence<T> implements Shrinkable<T> {

	private final List<Shrinkable<T>> steps = new ArrayList<>();

	public ShrinkableSequence() {
	}

	public ShrinkableSequence(List<Shrinkable<T>> steps) {
		steps.forEach(this::addStep);
	}

	public void addStep(Shrinkable<T> step) {
		steps.add(step);
	}

	public List<Shrinkable<T>> steps() {
		return Collections.unmodifiableList(steps);
	}

	@Override
	public Optional<ShrinkResult<T>> shrink(Predicate<T> falsifier) {
		Optional<ShrinkResult<T>> lastFalsified = Optional.empty();
		for (Shrinkable<T> shrinkValue : steps) {
			Optional<ShrinkResult<T>> shrinkResult = shrinkValue.shrink(falsifier);
			if (shrinkResult.isPresent()) {
				lastFalsified = shrinkResult;
			} else {
				break;
			}
		}
		return lastFalsified;
	}

}
