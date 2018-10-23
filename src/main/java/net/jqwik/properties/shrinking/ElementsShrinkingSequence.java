package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class ElementsShrinkingSequence<T> implements ShrinkingSequence<List<T>> {
	private final Falsifier<List<T>> listFalsifier;
	private final List<FalsificationResult<T>> currentResults;
	private final Function<List<Shrinkable<T>>, ShrinkingDistance> distanceFunction;

	private int currentShrinkingPosition = 0;
	private ShrinkingSequence<T> currentShrinkingSequence = null;
	private Throwable currentThrowable;

	public ElementsShrinkingSequence(
		List<Shrinkable<T>> currentElements,
		Throwable originalError,
		Falsifier<List<T>> listFalsifier,
		Function<List<Shrinkable<T>>, ShrinkingDistance> distanceFunction
	) {
		this.currentResults =
			currentElements
				.stream()
				.map(shrinkable -> FalsificationResult.falsified(shrinkable, originalError))
				.collect(Collectors.toList());
		this.currentThrowable = originalError;
		this.listFalsifier = listFalsifier;
		this.distanceFunction = distanceFunction;
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<List<T>>> falsifiedReporter) {
		if (isShrinkingDone())
			return false;
		return shrinkCurrentPosition(count, falsifiedReporter);
	}

	private boolean isShrinkingDone() {
		return currentShrinkingPosition >= currentResults.size();
	}

	private boolean shrinkCurrentPosition(Runnable count, Consumer<FalsificationResult<List<T>>> falsifiedReporter) {
		if (currentShrinkingSequence == null) {
			currentShrinkingSequence = createShrinkingSequence(currentShrinkingPosition);
		}
		if (!currentShrinkingSequence.next(count, currentFalsifiedReporter(falsifiedReporter))) {
			return advanceToNextShrinkingPosition(count, falsifiedReporter);
		}
		replaceCurrentPosition(currentShrinkingSequence.current());
		return true;
	}

	private boolean advanceToNextShrinkingPosition(
		Runnable count, Consumer<FalsificationResult<List<T>>> falsifiedReporter
	) {
		currentShrinkingSequence = null;
		currentShrinkingPosition++;
		return next(count, falsifiedReporter);
	}

	private Consumer<FalsificationResult<T>> currentFalsifiedReporter(
		Consumer<FalsificationResult<List<T>>> listReporter
	) {
		if (isShrinkingDone()) return ignore -> {};
		return resultOnCurrentShrinkingPosition -> { //
			listReporter.accept(resultOnCurrentShrinkingPosition.map(shrinkable -> shrinkable.map(valueOnCurrentShrinkingPosition -> {
				List<T> values = toValueList(currentResults);
				values.set(currentShrinkingPosition, resultOnCurrentShrinkingPosition.value());
				return values;
			})));
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

	private Shrinkable<List<T>> createCurrent(List<FalsificationResult<T>> falsificationResults) {
		return new Shrinkable<List<T>>() {
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
				return distanceFunction.apply(toShrinkableList(falsificationResults));
			}
		};
	}

	private List<T> toValueList(List<FalsificationResult<T>> results) {
		return results
				   .stream()
				   .map(FalsificationResult::value)
				   .collect(Collectors.toList());
	}

	private List<Shrinkable<T>> toShrinkableList(List<FalsificationResult<T>> results) {
		return results
				   .stream()
				   .map(FalsificationResult::shrinkable)
				   .collect(Collectors.toList());
	}

}
