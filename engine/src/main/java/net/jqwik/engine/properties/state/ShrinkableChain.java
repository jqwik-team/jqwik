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
	private final List<Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>>> chainGenerators;
	private final int maxSize;
	private final List<Iteration<T>> iterations;

	public ShrinkableChain(
		long randomSeed,
		Supplier<T> initialSupplier,
		List<Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>>> chainGenerators,
		int maxSize
	) {
		this(randomSeed, initialSupplier, chainGenerators, maxSize, new ArrayList<>());
	}

	private ShrinkableChain(
		long randomSeed, Supplier<T> initialSupplier,
		List<Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>>> chainGenerators,
		int maxSize,
		List<Iteration<T>> iterations
	) {
		this.randomSeed = randomSeed;
		this.initialSupplier = initialSupplier;
		this.chainGenerators = chainGenerators;
		this.maxSize = maxSize;
		this.iterations = iterations;
	}

	@Override
	public Chain<T> value() {
		return new ChainInstance();
	}

	@Override
	public Stream<Shrinkable<Chain<T>>> shrink() {
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
			Shrinkable<Chains.Mutator<T>> element = iteration.shrinkable;
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
		// ranges.add(Tuple.of(0, end));
		return ranges;
	}

	private ShrinkableChain<T> newShrinkableChain(List<Iteration<T>> shrunkIterations, int maxSize) {
		return new ShrinkableChain<>(
			randomSeed,
			initialSupplier,
			chainGenerators,
			maxSize,
			shrunkIterations
		);
	}

	@Override
	public ShrinkingDistance distance() {
		List<Shrinkable<Chains.Mutator<T>>> shrinkablesForDistance = new ArrayList<>();
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
		public int countIterations() {
			return iterations.size();
		}

		@Override
		public int maxSize() {
			return maxSize;
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

			Shrinkable<Chains.Mutator<T>> next = null;
			if (steps < iterations.size()) {
				next = rerunStep(nextSeed);
			} else {
				next = runNewStep(nextSeed);
			}
			steps++;
			current = next.value().apply(current);
			return current;
		}

		private Shrinkable<Chains.Mutator<T>> rerunStep(long nextSeed) {
			Iteration<T> iteration = iterations.get(steps);
			return iteration.shrinkable;
		}

		private Shrinkable<Chains.Mutator<T>> runNewStep(long nextSeed) {
			AtomicBoolean stateHasBeenAccessed = new AtomicBoolean(false);
			Supplier<T> currentSupplier = () -> {
				stateHasBeenAccessed.set(true);
				return current;
			};
			Random random = SourceOfRandomness.newRandom(nextSeed);
			Arbitrary<Chains.Mutator<T>> arbitrary = nextMutatorArbitrary(currentSupplier, random);
			RandomGenerator<Chains.Mutator<T>> generator = arbitrary.generator(maxSize);
			Shrinkable<Chains.Mutator<T>> next = generator.next(random);
			iterations.add(new Iteration<>(nextSeed, stateHasBeenAccessed.get(), next));
			return next;
		}

		private Arbitrary<Chains.Mutator<T>> nextMutatorArbitrary(Supplier<T> currentSupplier, Random random) {
			return MaxTriesLoop.loop(
				() -> true,
				arbitrary -> {
					int generatorIndex = random.nextInt(chainGenerators.size());
					Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>> chainGenerator = chainGenerators.get(generatorIndex);
					arbitrary = chainGenerator.apply(currentSupplier);
					if (arbitrary == null) {
						return Tuple.of(false, arbitrary);
					}
					return Tuple.of(true, arbitrary);
				},
				maxMisses -> {
					String message = String.format("Could not generate a mutator after %s tries.", maxMisses);
					return new JqwikException(message);
				},
				1000
			);
		}
	}

	private static class Iteration<T> {
		final long randomSeed;
		final boolean stateHasBeenAccessed;
		final Shrinkable<Chains.Mutator<T>> shrinkable;

		private Iteration(
			long randomSeed,
			boolean stateHasBeenAccessed,
			Shrinkable<Chains.Mutator<T>> shrinkable
		) {
			this.randomSeed = randomSeed;
			this.stateHasBeenAccessed = stateHasBeenAccessed;
			this.shrinkable = shrinkable;
		}

		@Override
		public String toString() {
			return String.format("Iteration[accessState=%s, mutator=%s]", stateHasBeenAccessed, shrinkable.value());
		}
	}
}
