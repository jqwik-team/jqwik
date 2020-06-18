package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

class CollectShrinkingSequence<T> implements ShrinkingSequence<List<T>> {

	private final Predicate<List<T>> until;
	private final Falsifier<List<T>> falsifier;

	private List<Shrinkable<T>> elements;
	private FalsificationResult<List<T>> current;
	private ShrinkingSequence<T> currentShrinkingSequence = null;
	private int currentShrinkingIndex = 0;

	CollectShrinkingSequence(List<Shrinkable<T>> elements, Predicate<List<T>> until, Falsifier<List<T>> falsifier) {
		this.elements = elements;
		this.until = until;
		this.falsifier = falsifier;
	}

	private ShrinkingSequence<T> getShrinkingSequence(int shrinkingPosition) {
		Falsifier<T> elementFalsifier = falsifier.map(value -> collectValues(shrinkingPosition, value));
		return elements.get(shrinkingPosition).shrink(elementFalsifier);
	}

	private List<Shrinkable<T>> collectShrinkables(int replaceIndex, Shrinkable<T> replaceShrinkable) {
		List<Shrinkable<T>> collectedShrinkables = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			if (until.test(toValues(collectedShrinkables))) {
				break;
			}
			Shrinkable<T> shrinkable = elements.get(i);
			if (i == replaceIndex) {
				shrinkable = replaceShrinkable;
			}
			collectedShrinkables.add(shrinkable);
		}
		return collectedShrinkables;
	}

	private List<T> toValues(List<Shrinkable<T>> collectedShrinkables) {
		return collectedShrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	private List<T> collectValues(int replaceIndex, T replaceValue) {
		Shrinkable<T> replaceShrinkable = Shrinkable.unshrinkable(replaceValue);
		return toValues(collectShrinkables(replaceIndex, replaceShrinkable));
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<List<T>>> falsifiedReporter) {
		while (currentShrinkingIndex < elements.size()) {
			if (currentShrinkingSequence == null) {
				currentShrinkingSequence = getShrinkingSequence(currentShrinkingIndex);
			}
			Consumer<FalsificationResult<T>> elementReporter = createElementReporter(falsifiedReporter, currentShrinkingIndex);
			boolean next = currentShrinkingSequence.next(count, elementReporter);
			if (next) {
				elements = collectShrinkables(currentShrinkingIndex, currentShrinkingSequence.current().shrinkable());
				current = currentShrinkingSequence.current()
												  .map(shrinkable -> new CollectShrinkable<>(elements, until));
			} else {
				currentShrinkingSequence = null;
				currentShrinkingIndex++;
				continue;
			}
			return true;
		}
		return false;
	}

	private Consumer<FalsificationResult<T>> createElementReporter(
		Consumer<FalsificationResult<List<T>>> falsifiedReporter,
		int elementIndex
	) {
		return elementResult -> {
			FalsificationResult<List<T>> listResult = elementResult.map(
				shrinkable -> {
					return new CollectShrinkable<>(collectShrinkables(elementIndex, shrinkable), until);
				});
			falsifiedReporter.accept(listResult);
		};
	}

	@Override
	public FalsificationResult<List<T>> current() {
		return current;
	}

	@Override
	public void init(FalsificationResult<List<T>> initialCurrent) {
		this.current = initialCurrent;
	}
}
