package net.jqwik.properties.newShrinking;

import java.util.*;
import java.util.stream.*;

public class ElementsShrinkingSequence<T> implements ShrinkingSequence<List<T>> {
	private final Falsifier<List<T>> listFalsifier;
	private final List<NShrinkable<T>> currentElements;

	private int currentShrinkingPosition = 0;
	private ShrinkingSequence<T> currentShrinkingSequence = null;

	public ElementsShrinkingSequence(List<NShrinkable<T>> currentElements, Falsifier<List<T>> listFalsifier) {
		this.currentElements = new ArrayList<>(currentElements);
		this.listFalsifier = listFalsifier;
	}

	@Override
	public boolean next(Runnable count) {
		if (currentShrinkingPosition >= currentElements.size())
			return false;
		return shrinkCurrentPosition(count);
	}

	private boolean shrinkCurrentPosition(Runnable count) {
		if (currentShrinkingSequence == null) {
			currentShrinkingSequence = createShrinkingSequence(currentShrinkingPosition);
		}
		if (!currentShrinkingSequence.next(count)) {
			return advanceToNextShrinkingPosition(count);
		}
		replaceCurrentPosition(currentShrinkingSequence.current());
		return true;
	}

	private boolean advanceToNextShrinkingPosition(Runnable count) {
		currentShrinkingSequence = null;
		currentShrinkingPosition++;
		return next(count);
	}

	private void replaceCurrentPosition(NShrinkable<T> shrinkable) {
		currentElements.set(currentShrinkingPosition, shrinkable);
	}

	private ShrinkingSequence<T> createShrinkingSequence(int position) {
		NShrinkable<T> positionShrinkable = currentElements.get(position);
		Falsifier<T> positionFalsifier = falsifierForPosition(position, currentElements);
		return positionShrinkable.shrink(positionFalsifier);
	}

	private Falsifier<T> falsifierForPosition(int position, List<NShrinkable<T>> shrinkableElements) {
		return elementValue -> {
			List<T> effectiveParams = createList(shrinkableElements);
			effectiveParams.set(position, elementValue);
			return listFalsifier.test(effectiveParams);
		};
	}

	@Override
	public NShrinkable<List<T>> current() {
		return createCurrent(currentElements);
	}

	private NShrinkable<List<T>> createCurrent(List<NShrinkable<T>> listOfShrinkables) {
		return new NShrinkable<List<T>>() {
			@Override
			public List<T> value() {
				return createList(listOfShrinkables);
			}

			@Override
			public ShrinkingSequence<List<T>> shrink(Falsifier<List<T>> falsifier) {
				return ElementsShrinkingSequence.this;
			}

			@Override
			public ShrinkingDistance distance() {
				return ShrinkingDistance.forCollection(listOfShrinkables);
			}
		};
	}

	private List<T> createList(List<NShrinkable<T>> shrinkables) {
		return shrinkables
			.stream()
			.map(NShrinkable::value)
			.collect(Collectors.toList());
	}

}
