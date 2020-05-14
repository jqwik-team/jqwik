package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

class ShrinkOneElementAfterTheOtherSequence<T> implements ShrinkingSequence<List<T>> {
	private final Falsifier<List<T>> listFalsifier;
	private final List<FalsificationResult<T>> currentResults;
	private final Function<List<Shrinkable<T>>, ShrinkingDistance> distanceFunction;

	private int currentShrinkingPosition = 0;
	private ShrinkingSequence<T> currentShrinkingSequence = null;
	private Throwable currentThrowable;

	/**
	 * Determines if jqwik tries to shrink all elements individually once more.
	 * This is important if parameters depend on each other, e.g. shrinking
	 * the second param allows the first param to be further shrinked.
	 *
	 * <p>
	 * Initial value is set to true so that shrinking of elements is tried at least once.
	 *
	 */
	private final AtomicBoolean shrinkingOccurredInPreviousRound = new AtomicBoolean(true);

	ShrinkOneElementAfterTheOtherSequence(
		List<Shrinkable<T>> currentElements,
		Falsifier<List<T>> listFalsifier,
		Function<List<Shrinkable<T>>, ShrinkingDistance> distanceFunction
	) {
		this.currentResults =
			currentElements
				.stream()
				.map(FalsificationResult::falsified)
				.collect(Collectors.toList());
		this.listFalsifier = listFalsifier;
		this.distanceFunction = distanceFunction;
	}

	@Override
	public void init(FalsificationResult<List<T>> initialCurrent) {
		this.currentThrowable = initialCurrent.throwable().orElse(null);

		for (int i = 0; i < currentResults.size(); i++) {
			FalsificationResult<T> newResult = FalsificationResult.falsified(currentResults.get(i).shrinkable(), currentThrowable);
			currentResults.set(i, newResult);
		}
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<List<T>>> falsifiedReporter) {
		if (isShrinkingDone())
			return false;
		Consumer<FalsificationResult<List<T>>> trackShrinkingReporter = result -> {
			shrinkingOccurredInPreviousRound.set(true);
			falsifiedReporter.accept(result);
		};
		return shrinkCurrentPosition(count, trackShrinkingReporter);
	}

	private boolean isShrinkingDone() {
		return currentResults.isEmpty() || (currentShrinkingPosition >= currentResults.size() && !shrinkingOccurredInPreviousRound.get());
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
		if (currentShrinkingPosition >= currentResults.size()) {
			if (shrinkingOccurredInPreviousRound.get()) {
				currentShrinkingPosition = 0;
				shrinkingOccurredInPreviousRound.set(false);
			}
		}
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
			return listFalsifier.execute(effectiveParams);
		};
	}

	@Override
	public FalsificationResult<List<T>> current() {
		return FalsificationResult.falsified(createCurrent(currentResults), currentThrowable);
	}

	private Shrinkable<List<T>> createCurrent(List<FalsificationResult<T>> falsificationResults) {
		return new Shrinkable<List<T>>() {
			final List<T> value = toValueList(falsificationResults);

			@Override
			public List<T> value() {
				return value;
			}

			@Override
			public ShrinkingSequence<List<T>> shrink(Falsifier<List<T>> falsifier) {
				return ShrinkOneElementAfterTheOtherSequence.this;
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
