package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

/**
 * This can be used to shrink the individual shrinkable elements in a list
 * without shrinking the size of the list and keeping the order:
 * <ul>
 *     <li>The actual elements of a container (list, set, action sequence)</li>
 *     <li>All shrinkable parameters of a property</li>
 * </ul>
 */
public class ShrinkElementsSequence<T> implements ShrinkingSequence<List<T>> {

	private final List<Shrinkable<T>> currentShrinkables;
	private final Falsifier<List<T>> listFalsifier;
	private final Function<List<Shrinkable<T>>, ShrinkingDistance> distanceFunction;
	private ShrinkOneElementAfterTheOtherSequence<T> nextSequence;
	private Throwable currentThrowable;
	private Map<Integer, Integer> tableOfDuplicates = new HashMap<>();

	public ShrinkElementsSequence(
		List<Shrinkable<T>> currentShrinkables,
		Falsifier<List<T>> listFalsifier,
		Function<List<Shrinkable<T>>, ShrinkingDistance> distanceFunction
	) {
		this.currentShrinkables = currentShrinkables;
		this.listFalsifier = listFalsifier;
		this.distanceFunction = distanceFunction;
		buildTableOfDuplicates();
	}

	private void buildTableOfDuplicates() {
		for (int i = 0; i < currentShrinkables.size(); i++) {
			for (int j = i + 1; j < currentShrinkables.size(); j++) {
				Shrinkable<T> iShrinkable = currentShrinkables.get(i);
				Shrinkable<T> jShrinkable = currentShrinkables.get(j);
				if (areDuplicates(iShrinkable, jShrinkable)) {
					tableOfDuplicates.put(i, j);
				}
			}
		}
		System.out.println("###############################");
		System.out.println(tableOfDuplicates);
	}

	private boolean areDuplicates(Shrinkable<T> iShrinkable, Shrinkable<T> jShrinkable) {
		return iShrinkable.equals(jShrinkable);
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<List<T>>> falsifiedReporter) {
		if (doneWithShrinkingDuplicates()) {
			return shrinkOneElementAfterTheOther(count, falsifiedReporter);
		} else {
			return shrinkDuplicates(count, falsifiedReporter);
		}
	}

	private ShrinkOneElementAfterTheOtherSequence<T> nextSequence() {
		if (nextSequence == null) {
			nextSequence = new ShrinkOneElementAfterTheOtherSequence<>(currentShrinkables, listFalsifier, distanceFunction);
			nextSequence.init(createCurrent());
		}
		return nextSequence;
	}

	private boolean shrinkOneElementAfterTheOther(Runnable count, Consumer<FalsificationResult<List<T>>> falsifiedReporter) {
		return nextSequence().next(count, falsifiedReporter);
	}

	private boolean shrinkDuplicates(Runnable count, Consumer<FalsificationResult<List<T>>> falsifiedReporter) {
		if (tableOfDuplicates.isEmpty()) {
			return false;
		}
		Map.Entry<Integer, Integer> indexPair = tableOfDuplicates.entrySet().iterator().next();
		boolean shrinkResult = shrinkPair(indexPair, count, falsifiedReporter);
		if (shrinkResult) {
			return true;
		} else {
			tableOfDuplicates.remove(indexPair.getKey());
			return shrinkDuplicates(count, falsifiedReporter);
		}
	}

	private boolean shrinkPair(
		Map.Entry<Integer, Integer> indexPair,
		Runnable count, Consumer<FalsificationResult<List<T>>> falsifiedReporter
	) {
		List<Shrinkable<T>> current = new ArrayList<>(currentShrinkables);
		boolean wasShrunk = false;
		int firstIndex = indexPair.getKey();
		int secondIndex = indexPair.getValue();

		while (true) {

			Shrinkable<T> firstShrinkable = current.get(firstIndex);
			Shrinkable<T> secondShrinkable = current.get(secondIndex);

			Optional<Shrinkable<T>> firstCandidate = candidate(firstShrinkable);
			Optional<Shrinkable<T>> secondCandidate = candidate(secondShrinkable);

			if (firstCandidate.isPresent() && secondCandidate.isPresent()) {

				Shrinkable<T> firstShrunk = firstCandidate.get();
				Shrinkable<T> secondShrunk = secondCandidate.get();

				System.out.println(String.format("#### Trying %s:%s", firstShrunk, secondShrunk));

				if (areDuplicates(firstShrunk, secondShrunk)) {
					ArrayList<Shrinkable<T>> toTry = new ArrayList<>(current);
					toTry.set(firstIndex, firstShrunk);
					toTry.set(secondIndex, secondShrunk);
					FalsificationResult<List<T>> result = listFalsifier.falsify(toShrinkableList(toTry));

					if (result.status() == FalsificationResult.Status.FALSIFIED) {
						System.out.println(result);
						currentThrowable = result.throwable().orElse(null);
						count.run();
						falsifiedReporter.accept(result);
						current = toTry;
						wasShrunk = true;
					}
				} else {
					break;
				}

			} else {
				break;
			}
		}

		if (wasShrunk) {
			currentShrinkables.set(firstIndex, current.get(firstIndex));
			currentShrinkables.set(secondIndex, current.get(secondIndex));
		}

		return wasShrunk;
//		return false;
	}

	private Optional<Shrinkable<T>> candidate(Shrinkable<T> toShrink) {
		ShrinkingSequence<T> sequence = toShrink.shrink(t -> false);
		if (sequence.next(() -> {}, tFalsificationResult -> {})) {
			return Optional.of(sequence.current().shrinkable());
		} else {
			return Optional.empty();
		}
	}

	private boolean doneWithShrinkingDuplicates() {
//		return true;
		return tableOfDuplicates.isEmpty();
	}

	@Override
	public FalsificationResult<List<T>> current() {
		if (doneWithShrinkingDuplicates()) {
			return nextSequence().current();
		} else {
			return createCurrent();
		}
	}

	private FalsificationResult<List<T>> createCurrent() {
		return FalsificationResult.falsified(toShrinkableList(currentShrinkables), currentThrowable);
	}

	private List<T> toValueList(List<Shrinkable<T>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	private Shrinkable<List<T>> toShrinkableList(List<Shrinkable<T>> shrinkables) {
		return new Shrinkable<List<T>>() {
			final List<T> value = toValueList(shrinkables);

			@Override
			public List<T> value() {
				return value;
			}

			@Override
			public ShrinkingSequence<List<T>> shrink(Falsifier<List<T>> falsifier) {
				return ShrinkElementsSequence.this;
			}

			@Override
			public ShrinkingDistance distance() {
				return distanceFunction.apply(ShrinkElementsSequence.this.currentShrinkables);
			}
		};
	}

	@Override
	public void init(FalsificationResult<List<T>> initialCurrent) {
		this.currentThrowable = initialCurrent.throwable().orElse(null);
	}
}
