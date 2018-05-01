package net.jqwik.properties.newShrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class ElementsShrinkingSequence<T> implements ShrinkingSequence<List<T>> {
	private final Falsifier<List<T>> listFalsifier;
	private final List<FalsificationResult<T>> currentResults;

	private int currentShrinkingPosition = 0;
	private ShrinkingSequence<T> currentShrinkingSequence = null;
	private Throwable currentThrowable = null;

	public ElementsShrinkingSequence(
		List<NShrinkable<T>> currentElements, Throwable originalError, Falsifier<List<T>> listFalsifier
	) {
		this.currentResults = currentElements
			.stream()
			.map(shrinkable -> FalsificationResult.falsified(shrinkable, originalError))
			.collect(Collectors.toList());
		this.currentThrowable = originalError;
		this.listFalsifier = listFalsifier;
	}

	@Override
	public boolean next(Runnable count, Consumer<List<T>> reportFalsified) {
		if (isShrinkingDone())
			return false;
		return shrinkCurrentPosition(count, reportFalsified);
	}

	private boolean isShrinkingDone() {
		return currentShrinkingPosition >= currentResults.size();
	}

	private boolean shrinkCurrentPosition(Runnable count, Consumer<List<T>> reportFalsified) {
		if (currentShrinkingSequence == null) {
			currentShrinkingSequence = createShrinkingSequence(currentShrinkingPosition);
		}
		if (!currentShrinkingSequence.next(count, currentFalsifiedReporter(reportFalsified))) {
			return advanceToNextShrinkingPosition(count, reportFalsified);
		}
		replaceCurrentPosition(currentShrinkingSequence.current());
		return true;
	}

	private boolean advanceToNextShrinkingPosition(Runnable count, Consumer<List<T>> reportFalsified) {
		currentShrinkingSequence = null;
		currentShrinkingPosition++;
		return next(count, reportFalsified);
	}

	private Consumer<T> currentFalsifiedReporter(Consumer<List<T>> listReporter) {
		if (isShrinkingDone()) return ignore -> {};
		return valueOnCurrentShrinkingPosition -> {
			List<T> values = toValueList(currentResults);
			values.set(currentShrinkingPosition, valueOnCurrentShrinkingPosition);
			listReporter.accept(values);
		};
	}

	private void replaceCurrentPosition(FalsificationResult<T> falsificationResult) {
		currentResults.set(currentShrinkingPosition, falsificationResult);
		currentThrowable = falsificationResult.throwable().orElse(null);
	}

	private ShrinkingSequence<T> createShrinkingSequence(int position) {
		FalsificationResult<T> positionResult = currentResults.get(position);
		Falsifier<T> positionFalsifier = falsifierForPosition(position, currentResults);
		return positionResult.shrinkable().shrink(positionFalsifier);
	}

	private Falsifier<T> falsifierForPosition(int position, List<FalsificationResult<T>> falsificationResults) {
		return elementValue -> {
			List<T> effectiveParams = toValueList(falsificationResults);
			effectiveParams.set(position, elementValue);
			return listFalsifier.test(effectiveParams);
		};
	}

	@Override
	public FalsificationResult<List<T>> current() {
		return FalsificationResult.falsified(createCurrent(currentResults), currentThrowable);
	}

	private NShrinkable<List<T>> createCurrent(List<FalsificationResult<T>> falsificationResults) {
		return new NShrinkable<List<T>>() {
			@Override
			public List<T> value() {
				return toValueList(falsificationResults);
			}

			@Override
			public ShrinkingSequence<List<T>> shrink(Falsifier<List<T>> falsifier) {
				return ElementsShrinkingSequence.this;
			}

			@Override
			public ShrinkingDistance distance() {
				return ShrinkingDistance.forCollection(toShrinkableList(falsificationResults));
			}
		};
	}

	private List<T> toValueList(List<FalsificationResult<T>> results) {
		return results
			.stream()
			.map(FalsificationResult::value)
			.collect(Collectors.toList());
	}

	private List<NShrinkable<T>> toShrinkableList(List<FalsificationResult<T>> results) {
		return results
			.stream()
			.map(FalsificationResult::shrinkable)
			.collect(Collectors.toList());
	}

}
