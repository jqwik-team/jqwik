package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

public class ShrinkableChain<T> implements Shrinkable<Chain<T>> {

	private final long randomSeed;
	private final Supplier<T> initialSupplier;
	private final Function<Random, TransformerProvider<T>> providerGenerator;
	private final int maxSize;
	private final List<Iteration<T>> iterations;

	public ShrinkableChain(
		long randomSeed,
		Supplier<T> initialSupplier,
		Function<Random, TransformerProvider<T>> providerGenerator,
		int maxSize
	) {
		this(randomSeed, initialSupplier, providerGenerator, maxSize, new ArrayList<>());
	}

	private ShrinkableChain(
		long randomSeed, Supplier<T> initialSupplier,
		Function<Random, TransformerProvider<T>> providerGenerator,
		int maxSize,
		List<Iteration<T>> iterations
	) {
		this.randomSeed = randomSeed;
		this.initialSupplier = initialSupplier;
		this.providerGenerator = providerGenerator;
		this.maxSize = maxSize;
		this.iterations = iterations;
	}

	@Override
	public Chain<T> value() {
		return new ChainInstance();
	}

	@Override
	public Stream<Shrinkable<Chain<T>>> shrink() {
		return Stream.concat(
			shrinkMaxSize(),
			shrinkRanges()
		);
	}

	private Stream<Shrinkable<Chain<T>>> shrinkMaxSize() {
		if (iterations.size() < maxSize) {
			return Stream.of(newShrinkableChain(iterations, iterations.size()));
		} else {
			return Stream.empty();
		}
	}

	private Stream<Shrinkable<Chain<T>>> shrinkRanges() {
		return splitIntoRanges().stream()
								.flatMap(range -> shrinkIterationsRange(range.get1(), range.get2()));
	}

	private Stream<Shrinkable<Chain<T>>> shrinkIterationsRange(int startIndex, int endIndex) {
		List<Iteration<T>> iterationsRange = extractRange(startIndex, endIndex);
		return Stream.concat(
			shrinkAllSubRanges(startIndex, iterationsRange),
			shrinkOneAfterTheOther(startIndex, iterationsRange)
		);
	}

	private List<Iteration<T>> extractRange(int startIndex, int endIndex) {
		List<Iteration<T>> iterationsRange = new ArrayList<>();
		for (int i = 0; i < iterations.size(); i++) {
			if (i >= startIndex && i <= endIndex) {
				iterationsRange.add(iterations.get(i));
			}
		}
		return iterationsRange;
	}

	private Stream<Shrinkable<Chain<T>>> shrinkOneAfterTheOther(int startIndex, List<Iteration<T>> iterationsRange) {
		Stream<List<Iteration<T>>> shrunkRange = shrinkOneIterationAfterTheOther(iterationsRange);
		int restSize = iterations.size() - iterationsRange.size();
		return replaceRangeByShrunkRange(startIndex, shrunkRange, restSize);
	}

	private Stream<Shrinkable<Chain<T>>> shrinkAllSubRanges(int startIndex, List<Iteration<T>> iterationsRange) {
		Stream<List<Iteration<T>>> shrunkRange = shrinkToAllSubLists(iterationsRange);
		int restSize = iterations.size() - iterationsRange.size();
		return replaceRangeByShrunkRange(startIndex, shrunkRange, restSize);
	}

	private Stream<List<Iteration<T>>> shrinkOneIterationAfterTheOther(List<Iteration<T>> iterationsRange) {
		List<Stream<List<Iteration<T>>>> shrinkPerElementStreams = new ArrayList<>();
		for (int i = 0; i < iterationsRange.size(); i++) {
			int index = i;
			Iteration<T> iteration = iterationsRange.get(i);
			Shrinkable<Transformer<T>> element = iteration.shrinkable;
			Stream<List<Iteration<T>>> shrinkElement = element.shrink().flatMap(shrunkElement -> {
				List<Iteration<T>> iterationsCopy = new ArrayList<>(iterationsRange);
				iterationsCopy.set(index, new Iteration<>(iteration.randomSeed, iteration.stateHasBeenAccessed, shrunkElement));
				return Stream.of(iterationsCopy);
			});
			shrinkPerElementStreams.add(shrinkElement);
		}
		return JqwikStreamSupport.concat(shrinkPerElementStreams);
	}

	private Stream<Shrinkable<Chain<T>>> replaceRangeByShrunkRange(int startIndex, Stream<List<Iteration<T>>> shrunkRange, int restSize) {
		return shrunkRange.map(shrunkIterationsRange -> {
			List<Iteration<T>> shrunkIterations = new ArrayList<>();
			for (int i = 0; i < startIndex; i++) {
				shrunkIterations.add(iterations.get(i));
			}
			shrunkIterations.addAll(shrunkIterationsRange);
			int newMaxSize = restSize + shrunkIterationsRange.size();
			return newShrinkableChain(shrunkIterations, newMaxSize);
		});
	}

	private Stream<List<Iteration<T>>> shrinkToAllSubLists(List<Iteration<T>> iterations) {
		return new ComprehensiveSizeOfListShrinker()
			.shrink(iterations, 1);
	}

	private List<Tuple2<Integer, Integer>> splitIntoRanges() {
		List<Tuple2<Integer, Integer>> ranges = new ArrayList<>();
		// Move backwards to the next iteration with access to state
		int end = 0;
		for (int i = iterations.size() - 1; i >= 0; i--) {
			end = i;
			while (i >= 0) {
				Iteration<T> current = iterations.get(i);
				if (current.stateHasBeenAccessed || i == 0) {
					ranges.add(Tuple.of(i, end));
					break;
				}
				i--;
			}
		}
		return ranges;
	}

	private ShrinkableChain<T> newShrinkableChain(List<Iteration<T>> shrunkIterations, int newMaxSize) {
		return new ShrinkableChain<>(
			randomSeed,
			initialSupplier,
			providerGenerator,
			newMaxSize,
			shrunkIterations
		);
	}

	@Override
	public ShrinkingDistance distance() {
		List<Shrinkable<Transformer<T>>> shrinkablesForDistance = new ArrayList<>();
		for (int i = 0; i < maxSize; i++) {
			if (i < iterations.size()) {
				shrinkablesForDistance.add(iterations.get(i).shrinkable);
			} else {
				shrinkablesForDistance.add(Shrinkable.unshrinkable(t -> t));
			}
		}
		return ShrinkingDistance.forCollection(shrinkablesForDistance);
	}

	@Override
	public String toString() {
		return String.format("ShrinkableChain[maxSize=%s, iterations=%s]", maxSize, iterations);
	}

	private class ChainInstance implements Chain<T> {

		@Override
		public Iterator<T> start() {
			return new ChainIterator(initialSupplier.get());
		}

		@Override
		public int countTransformations() {
			return iterations.size();
		}

		@Override
		public int maxTransformations() {
			return maxSize;
		}

		@Override
		public List<String> transformations() {
			return iterations.stream().map(i -> i.shrinkable.value().transformation()).collect(Collectors.toList());
		}
	}

	private class ChainIterator implements Iterator<T> {

		private final Random random = SourceOfRandomness.newRandom(randomSeed);
		private int steps = 0;
		private T current;

		private ChainIterator(T initial) {
			this.current = initial;
		}

		@Override
		public boolean hasNext() {
			return steps < maxSize;
		}

		@Override
		public T next() {

			// Create deterministic random in order to reuse in shrinking
			long nextSeed = random.nextLong();
			Shrinkable<Transformer<T>> next = null;

			synchronized (ShrinkableChain.this) {
				if (steps < iterations.size()) {
					next = rerunStep(nextSeed);
				} else {
					next = runNewStep(nextSeed);
				}
				steps++;
			}

			current = next.value().apply(current);
			return current;
		}

		private Shrinkable<Transformer<T>> rerunStep(long nextSeed) {
			Iteration<T> iteration = iterations.get(steps);
			return iteration.shrinkable;
		}

		private Shrinkable<Transformer<T>> runNewStep(long nextSeed) {
			AtomicBoolean stateHasBeenAccessed = new AtomicBoolean(false);
			Supplier<T> currentSupplier = () -> {
				stateHasBeenAccessed.set(true);
				return current;
			};
			Random random = SourceOfRandomness.newRandom(nextSeed);
			Arbitrary<Transformer<T>> arbitrary = nextTransformerArbitrary(currentSupplier, random);
			RandomGenerator<Transformer<T>> generator = arbitrary.generator(maxSize);
			Shrinkable<Transformer<T>> next = generator.next(random);
			iterations.add(new Iteration<>(nextSeed, stateHasBeenAccessed.get(), next));
			return next;
		}

		private Arbitrary<Transformer<T>> nextTransformerArbitrary(Supplier<T> currentStateSupplier, Random random) {
			return MaxTriesLoop.loop(
				() -> true,
				arbitrary -> {
					Function<Supplier<T>, Arbitrary<Transformer<T>>> chainGenerator = providerGenerator.apply(random);
					arbitrary = chainGenerator.apply(currentStateSupplier);
					if (arbitrary == null) {
						return Tuple.of(false, arbitrary);
					}
					return Tuple.of(true, arbitrary);
				},
				maxMisses -> {
					String message = String.format("Could not generate a transformer after %s tries.", maxMisses);
					return new JqwikException(message);
				},
				1000
			);
		}
	}

	private static class Iteration<T> {
		final long randomSeed;
		final boolean stateHasBeenAccessed;
		final Shrinkable<Transformer<T>> shrinkable;

		private Iteration(
			long randomSeed,
			boolean stateHasBeenAccessed,
			Shrinkable<Transformer<T>> shrinkable
		) {
			this.randomSeed = randomSeed;
			this.stateHasBeenAccessed = stateHasBeenAccessed;
			this.shrinkable = shrinkable;
		}

		@Override
		public String toString() {
			return String.format("Iteration[accessState=%s, transformer=%s]", stateHasBeenAccessed, shrinkable.value());
		}
	}
}
