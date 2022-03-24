package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

class ShrinkableChainShrinker<T> {

	private final ShrinkableChain<T> shrinkable;
	private final List<ShrinkableChainIteration<T>> iterations;
	private final int maxTransformations;

	ShrinkableChainShrinker(ShrinkableChain<T> shrinkableChain, List<ShrinkableChainIteration<T>> iterations, int maxTransformations) {
		this.shrinkable = shrinkableChain;
		this.iterations = iterations;
		this.maxTransformations = maxTransformations;
	}

	public Stream<Shrinkable<Chain<T>>> shrink() {
		if (iterations.isEmpty()) {
			// Do not try to shrink chains that have not run at all
			return Stream.empty();
		}
		return JqwikStreamSupport.concat(
			shrinkMaxTransformations(),
			shrinkLastStateAccessingTransformer(),
			shrinkRanges()
		);
	}

	private Stream<Shrinkable<Chain<T>>> shrinkMaxTransformations() {
		if (iterations.size() < maxTransformations) {
			return Stream.of(newShrinkableChain(iterations, iterations.size()));
		} else {
			return Stream.empty();
		}
	}

	private Stream<Shrinkable<Chain<T>>> shrinkLastStateAccessingTransformer() {
		for (int i = iterations.size() - 1; i >= 0; i--) {
			ShrinkableChainIteration<T> iteration = iterations.get(i);
			if (iteration.stateHasBeenAccessed) {
				List<ShrinkableChainIteration<T>> shrunkIterations = new ArrayList<>(iterations);
				shrunkIterations.remove(i);
				return Stream.of(newShrinkableChain(shrunkIterations, shrunkIterations.size()));
			}
		}
		return Stream.empty();
	}

	private Stream<Shrinkable<Chain<T>>> shrinkRanges() {
		return splitIntoRanges().stream()
								.flatMap(range -> shrinkIterationsRange(range.get1(), range.get2()));
	}

	private Stream<ShrinkableChain<T>> shrinkIterationsRange(int startIndex, int endIndex) {
		List<ShrinkableChainIteration<T>> iterationsRange = extractRange(startIndex, endIndex);
		return Stream.concat(
			shrinkAllSubRanges(startIndex, iterationsRange),
			shrinkOneAfterTheOther(startIndex, iterationsRange)
		);
	}

	private List<ShrinkableChainIteration<T>> extractRange(int startIndex, int endIndex) {
		List<ShrinkableChainIteration<T>> iterationsRange = new ArrayList<>();
		for (int i = 0; i < iterations.size(); i++) {
			if (i >= startIndex && i <= endIndex) {
				iterationsRange.add(iterations.get(i));
			}
		}
		return iterationsRange;
	}

	private Stream<ShrinkableChain<T>> shrinkOneAfterTheOther(int startIndex, List<ShrinkableChainIteration<T>> iterationsRange) {
		Stream<List<ShrinkableChainIteration<T>>> shrunkRange = shrinkOneIterationAfterTheOther(iterationsRange);
		int restSize = iterations.size() - iterationsRange.size();
		return replaceRangeByShrunkRange(startIndex, shrunkRange, restSize);
	}

	private Stream<ShrinkableChain<T>> shrinkAllSubRanges(int startIndex, List<ShrinkableChainIteration<T>> iterationsRange) {
		Stream<List<ShrinkableChainIteration<T>>> shrunkRange = shrinkToAllSubLists(iterationsRange);
		int restSize = iterations.size() - iterationsRange.size();
		return replaceRangeByShrunkRange(startIndex, shrunkRange, restSize);
	}

	private Stream<List<ShrinkableChainIteration<T>>> shrinkOneIterationAfterTheOther(List<ShrinkableChainIteration<T>> iterationsRange) {
		List<Stream<List<ShrinkableChainIteration<T>>>> shrinkPerElementStreams = new ArrayList<>();
		for (int i = 0; i < iterationsRange.size(); i++) {
			int index = i;
			ShrinkableChainIteration<T> iteration = iterationsRange.get(i);
			Shrinkable<Transformer<T>> element = iteration.shrinkable;
			Stream<List<ShrinkableChainIteration<T>>> shrinkElement = element.shrink().flatMap(shrunkElement -> {
				List<ShrinkableChainIteration<T>> iterationsCopy = new ArrayList<>(iterationsRange);
				iterationsCopy.set(index, new ShrinkableChainIteration<>(iteration.randomSeed, iteration.stateHasBeenAccessed, shrunkElement));
				return Stream.of(iterationsCopy);
			});
			shrinkPerElementStreams.add(shrinkElement);
		}
		return JqwikStreamSupport.concat(shrinkPerElementStreams);
	}

	private Stream<ShrinkableChain<T>> replaceRangeByShrunkRange(
		int startIndex,
		Stream<List<ShrinkableChainIteration<T>>> shrunkRange,
		int restSize
	) {
		return shrunkRange.map(shrunkIterationsRange -> {
			List<ShrinkableChainIteration<T>> shrunkIterations = new ArrayList<>();
			for (int i = 0; i < startIndex; i++) {
				shrunkIterations.add(iterations.get(i));
			}
			shrunkIterations.addAll(shrunkIterationsRange);
			int newMaxSize = restSize + shrunkIterationsRange.size();
			return newShrinkableChain(shrunkIterations, newMaxSize);
		});
	}

	private Stream<List<ShrinkableChainIteration<T>>> shrinkToAllSubLists(List<ShrinkableChainIteration<T>> iterations) {
		return new ComprehensiveSizeOfListShrinker().shrink(iterations, 1);
	}

	private List<Tuple2<Integer, Integer>> splitIntoRanges() {
		List<Tuple2<Integer, Integer>> ranges = new ArrayList<>();
		// Move backwards to the next iteration with access to state
		int end = 0;
		for (int i = iterations.size() - 1; i >= 0; i--) {
			end = i;
			while (i >= 0) {
				ShrinkableChainIteration<T> current = iterations.get(i);
				if (current.stateHasBeenAccessed || i == 0) {
					ranges.add(Tuple.of(i, end));
					break;
				}
				i--;
			}
		}
		return ranges;
	}

	private ShrinkableChain<T> newShrinkableChain(List<ShrinkableChainIteration<T>> shrunkIterations, int newMaxSize) {
		return shrinkable.cloneWith(
			shrunkIterations,
			newMaxSize
		);
	}

}
