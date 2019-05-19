package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

class CollectShrinkingSequence<T> implements ShrinkingSequence<List<T>> {

	private final List<Shrinkable<T>> elements;
	private final int maxSize;
	private final Predicate<List<T>> until;
	private final Falsifier<List<T>> falsifier;

	private FalsificationResult<List<T>> current;
	private ShrinkingSequence<T> currentShrinkingSequence = null;
	private int currentShrinkingIndex = 0;

	CollectShrinkingSequence(List<Shrinkable<T>> elements, Predicate<List<T>> until, Falsifier<List<T>> falsifier) {
		this.elements = elements;
		this.maxSize = elements.size();
		this.until = until;
		this.falsifier = falsifier;
	}

	private ShrinkingSequence<T> getShrinkingSequence(int shrinkingPosition) {
		Falsifier<T> elementFalsifier = value -> {
			List<T> currentValues = collectValues(shrinkingPosition, value);
			return falsifier.test(currentValues);
		};
		return elements.get(shrinkingPosition).shrink(elementFalsifier);
	}

	private List<Shrinkable<T>> currentShrinkables(int replaceIndex, Shrinkable<T> replaceShrinkable) {
		List<Shrinkable<T>> currentShrinkables = new ArrayList<>(elements);
		currentShrinkables.set(replaceIndex, replaceShrinkable);
		return currentShrinkables;
	}

	private List<T> collectValues(int replaceIndex, T replaceValue) {
		int i = 0;
		List<T> collectedValues = new ArrayList<>();
		while(!until.test(collectedValues) && i < maxSize) {
			T value = elements.get(i).value();
			if (i == replaceIndex) {
				value = replaceValue;
			}
			collectedValues.add(value);
			i++;
		}
		return collectedValues;
	}

	private List<T> createCurrent() {
		return elements.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<List<T>>> falsifiedReporter) {
		while (currentShrinkingIndex < elements.size()) {
			if (currentShrinkingSequence == null) {
				currentShrinkingSequence = getShrinkingSequence(currentShrinkingIndex);
			}
			Consumer<FalsificationResult<T>> elementReporter = elementResult -> {
				FalsificationResult<List<T>> listResult = elementResult.map(
					shrinkable -> new CollectShrinkable<>(currentShrinkables(currentShrinkingIndex, shrinkable), until));
				falsifiedReporter.accept(listResult);
			};
			boolean next = currentShrinkingSequence.next(count, elementReporter);
			if (next) {
				elements.set(currentShrinkingIndex, currentShrinkingSequence.current().shrinkable());

				current =
					currentShrinkingSequence
						.current()
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

	@Override
	public FalsificationResult<List<T>> current() {
		return current;
	}

	@Override
	public void init(FalsificationResult<List<T>> initialCurrent) {
		this.current = initialCurrent;
	}
}
