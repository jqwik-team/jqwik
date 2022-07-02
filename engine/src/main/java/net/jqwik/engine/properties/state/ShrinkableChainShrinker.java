package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
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
			shrinkRanges(),
			shrinkTransformersWithoutStateChange()
		);
	}

	private Stream<Shrinkable<Chain<T>>> shrinkTransformersWithoutStateChange() {
		int indexLastStateAccess = indexOfLastIterationWithStateAccess();
		if (indexLastStateAccess > 0) {
			// Don't try to shrink the last transformation with state access itself,
			// because it will be shrunk anyway
			List<Shrinkable<Chain<T>>> shrunkChains = new ArrayList<>();
			for (int i = 0; i < indexLastStateAccess; i++) {
				ShrinkableChainIteration<T> currentIteration = iterations.get(i);
				if (!currentIteration.changeState) {
					ArrayList<ShrinkableChainIteration<T>> shrunkIterations = new ArrayList<>(iterations);
					shrunkIterations.remove(i);
					shrunkChains.add(newShrinkableChain(shrunkIterations, maxTransformations - 1));
				}
			}
			return shrunkChains.stream();
		}
		return Stream.empty();
	}

	private int indexOfLastIterationWithStateAccess() {
		for (int i = iterations.size() - 1; i >= 0; i--) {
			ShrinkableChainIteration<T> iteration = iterations.get(i);
			if (iteration.accessState) {
				return i;
			}
		}
		return -1;
	}

	private Stream<Shrinkable<Chain<T>>> shrinkMaxTransformations() {
		if (iterations.size() < maxTransformations) {
			return Stream.of(newShrinkableChain(iterations, iterations.size()));
		} else {
			return Stream.empty();
		}
	}

	private Stream<Shrinkable<Chain<T>>> shrinkLastStateAccessingTransformer() {
		int indexLastIterationWithStateAccess = indexOfLastIterationWithStateAccess();
		if (indexLastIterationWithStateAccess >= 0) {
			List<ShrinkableChainIteration<T>> shrunkIterations = new ArrayList<>(iterations);
			shrunkIterations.remove(indexLastIterationWithStateAccess);
			return Stream.of(newShrinkableChain(shrunkIterations, maxTransformations - 1));
		}
		return Stream.empty();
	}

	private Stream<Shrinkable<Chain<T>>> shrinkRanges() {
		return splitIntoRanges().stream()
								.flatMap(range -> shrinkIterationsRange(range.get1(), range.get2()));
	}

	private Stream<ShrinkableChain<T>> shrinkIterationsRange(int startIndex, int endIndex) {
		List<ShrinkableChainIteration<T>> iterationsRange = extractRange(startIndex, endIndex);
		return JqwikStreamSupport.concat(
			shrinkAllSubRanges(startIndex, iterationsRange),
			shrinkOneAfterTheOther(startIndex, iterationsRange),
			shrinkPairs(startIndex, iterationsRange)
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

	private Stream<ShrinkableChain<T>> shrinkPairs(int startIndex, List<ShrinkableChainIteration<T>> iterationsRange) {
		Stream<List<ShrinkableChainIteration<T>>> shrunkRange = shrinkPairsOfIterations(iterationsRange);
		int restSize = iterations.size() - iterationsRange.size();
		return replaceRangeByShrunkRange(startIndex, shrunkRange, restSize);
	}

	private Stream<List<ShrinkableChainIteration<T>>> shrinkPairsOfIterations(List<ShrinkableChainIteration<T>> iterationsRange) {
		return Combinatorics
			.distinctPairs(iterationsRange.size())
			.flatMap(pair -> {
				ShrinkableChainIteration<T> first = iterationsRange.get(pair.get1());
				ShrinkableChainIteration<T> second = iterationsRange.get(pair.get2());
				return JqwikStreamSupport.zip(
					first.shrinkable.shrink(),
					second.shrinkable.shrink(),
					(Shrinkable<Transformer<T>> s1, Shrinkable<Transformer<T>> s2) -> {
						ArrayList<ShrinkableChainIteration<T>> newElements = new ArrayList<>(iterationsRange);
						newElements.set(pair.get1(), first.withShrinkable(s1));
						newElements.set(pair.get2(), second.withShrinkable(s2));
						return newElements;
					}
				);
			});
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
				iterationsCopy.set(index, iteration.withShrinkable(shrunkElement));
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
		Set<List<ShrinkableChainIteration<T>>> setOfSequences = new LinkedHashSet<>();
		for (int i = 0; i < iterations.size(); i++) {
			if (!isUnshrinkableEndOfChain(iterations.get(i))) {
				ArrayList<ShrinkableChainIteration<T>> newCandidate = new ArrayList<>(iterations);
				newCandidate.remove(i);
				setOfSequences.add(newCandidate);
			}
		}
		return setOfSequences.stream();
	}

	private boolean isUnshrinkableEndOfChain(ShrinkableChainIteration<T> iteration) {
		return isInfinite() && iteration.isEndOfChain();
	}

	private List<Tuple2<Integer, Integer>> splitIntoRanges() {
		List<Tuple2<Integer, Integer>> ranges = new ArrayList<>();
		// Move backwards to the next iteration with access to state
		int end = 0;
		for (int i = iterations.size() - 1; i >= 0; i--) {
			end = i;
			while (i >= 0) {
				ShrinkableChainIteration<T> current = iterations.get(i);
				if (current.accessState || i == 0) {
					ranges.add(Tuple.of(i, end));
					break;
				}
				i--;
			}
		}
		return ranges;
	}

	private ShrinkableChain<T> newShrinkableChain(List<ShrinkableChainIteration<T>> shrunkIterations, int newMaxTransformations) {
		int effectiveNewMax = isInfinite() ? -1 : newMaxTransformations;

		if (isInfinite() && !shrunkIterations.get(shrunkIterations.size() - 1).isEndOfChain()) {
			shrunkIterations.add(
				new ShrinkableChainIteration<>(null, false, Shrinkable.unshrinkable(Transformer.endOfChain()))
			);
		}

		return shrinkable.cloneWith(
			shrunkIterations,
			effectiveNewMax
		);
	}

	private boolean isInfinite() {
		return maxTransformations < 0;
	}

}
