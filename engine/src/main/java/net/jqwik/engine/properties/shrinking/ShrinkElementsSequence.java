package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

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
	private final List<Tuple2<Integer, Integer>> tableOfDuplicates;
	private ShrinkOneElementAfterTheOtherSequence<T> nextSequence;
	private Throwable currentThrowable;

	public ShrinkElementsSequence(
		List<Shrinkable<T>> currentShrinkables,
		Falsifier<List<T>> listFalsifier,
		Function<List<Shrinkable<T>>, ShrinkingDistance> distanceFunction
	) {
		this.currentShrinkables = currentShrinkables;
		this.listFalsifier = listFalsifier;
		this.distanceFunction = distanceFunction;
		this.tableOfDuplicates = buildTableOfDuplicates(currentShrinkables);
	}

	private static <T> List<Tuple2<Integer, Integer>> buildTableOfDuplicates(List<Shrinkable<T>> shrinkables) {
		List<Tuple2<Integer, Integer>> indicesOfDuplicateValues = new ArrayList<>();
		for (int i = 0; i < shrinkables.size(); i++) {
			for (int j = i + 1; j < shrinkables.size(); j++) {
				Shrinkable<T> iShrinkable = shrinkables.get(i);
				Shrinkable<T> jShrinkable = shrinkables.get(j);
				if (areDuplicates(iShrinkable, jShrinkable)) {
					indicesOfDuplicateValues.add(Tuple.of(i, j));
				}
			}
		}
		return indicesOfDuplicateValues;
	}

	private static <T> boolean areDuplicates(Shrinkable<T> first, Shrinkable<T> second) {
		return Objects.equals(first.value(), second.value()) && first.getClass().equals(second.getClass());
	}


	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<List<T>>> falsifiedReporter) {
		if (doneWithShrinkingDuplicates()) {
			return shrinkOneElementAfterTheOther(count, falsifiedReporter);
		} else {
			return shrinkDuplicates(count, falsifiedReporter);
		}
	}

	@Override
	public FalsificationResult<List<T>> current() {
		if (doneWithShrinkingDuplicates()) {
			return nextSequence().current();
		} else {
			return createCurrent();
		}
	}

	private boolean doneWithShrinkingDuplicates() {
		return tableOfDuplicates.isEmpty();
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
		if (doneWithShrinkingDuplicates()) {
			return false;
		}
		Tuple2<Integer, Integer> indexPair = tableOfDuplicates.get(0);
		tableOfDuplicates.remove(indexPair);
		if (shrinkPair(indexPair, count, falsifiedReporter)) {
			tableOfDuplicates.removeIf(pairInTable -> shareAnyIndex(pairInTable, indexPair));
			return true;
		} else {
			return next(count, falsifiedReporter);
		}
	}

	private boolean shareAnyIndex(Tuple2<Integer, Integer> pair1, Tuple2<Integer, Integer> pair2) {
		if (pair1.get1().equals(pair2.get1())) {
			return true;
		}
		if (pair1.get1().equals(pair2.get2())) {
			return true;
		}
		if (pair1.get2().equals(pair2.get1())) {
			return true;
		}
		return pair1.get2().equals(pair2.get2());
	}

	private boolean shrinkPair(
		Tuple2<Integer, Integer> indexPair,
		Runnable count, Consumer<FalsificationResult<List<T>>> falsifiedReporter
	) {
		List<Shrinkable<T>> current = new ArrayList<>(currentShrinkables);
		boolean wasShrunk = false;
		int firstIndex = indexPair.get1();
		int secondIndex = indexPair.get2();

		Shrinkable<T> firstShrinkable = current.get(firstIndex);
		Shrinkable<T> secondShrinkable = current.get(secondIndex);

		List<Tuple2<Shrinkable<T>, Shrinkable<T>>> candidatePairs = suggestions(firstShrinkable, secondShrinkable);

		for (Tuple2<Shrinkable<T>, Shrinkable<T>> candidatePair : candidatePairs) {

			Shrinkable<T> firstShrunk = candidatePair.get1();
			Shrinkable<T> secondShrunk = candidatePair.get2();

			ArrayList<Shrinkable<T>> toTry = new ArrayList<>(current);
			toTry.set(firstIndex, firstShrunk);
			toTry.set(secondIndex, secondShrunk);
			FalsificationResult<List<T>> result = listFalsifier.falsify(toShrinkableList(toTry));

			if (result.status() == FalsificationResult.Status.FALSIFIED) {
				currentThrowable = result.throwable().orElse(null);
				count.run();
				falsifiedReporter.accept(result);
				current = toTry;
				wasShrunk = true;
				break;
			}
		}

		if (wasShrunk) {
			currentShrinkables.set(firstIndex, current.get(firstIndex));
			currentShrinkables.set(secondIndex, current.get(secondIndex));
			// Shrink recursively with new values
			shrinkPair(indexPair, count, falsifiedReporter);
		}

		return wasShrunk;
	}

	private List<Tuple2<Shrinkable<T>, Shrinkable<T>>> suggestions(Shrinkable<T> first, Shrinkable<T> second) {
		List<Shrinkable<T>> suggestionsFirst = first.shrinkingSuggestions();
		List<Shrinkable<T>> suggestionsSecond = second.shrinkingSuggestions();

		List<Tuple2<Shrinkable<T>, Shrinkable<T>>> suggestions = new ArrayList<>();
		for (Shrinkable<T> firstSuggestion : suggestionsFirst) {
			for (Shrinkable<T> secondSuggestion : suggestionsSecond) {
				if (areDuplicates(firstSuggestion, secondSuggestion)) {
					suggestions.add(Tuple.of(firstSuggestion, secondSuggestion));
				}
			}
		}
		return suggestions;
	}

	private FalsificationResult<List<T>> createCurrent() {
		return FalsificationResult.falsified(toShrinkableList(currentShrinkables), currentThrowable);
	}

	private List<T> toValueList(List<Shrinkable<T>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	private Shrinkable<List<T>> toShrinkableList(List<Shrinkable<T>> shrinkables) {
		return new Shrinkable<List<T>>() {
			@Override
			public List<T> value() {
				return toValueList(shrinkables);
			}

			@Override
			public ShrinkingSequence<List<T>> shrink(Falsifier<List<T>> falsifier) {
				return new ShrinkElementsSequence<>(shrinkables, listFalsifier, distanceFunction);
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
