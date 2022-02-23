package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
import net.jqwik.api.stateful.*;
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
		// TODO: Shrink all trailing iterations without access to state together
		return shrinkAllIterations();
		// return Stream.concat(
		// 	shrinkLastIteration(),
		// 	shrinkButLastIteration()
		// );
	}

	private Stream<Shrinkable<Chain<T>>> shrinkAllIterations() {
		List<Tuple2<Integer, Integer>> shrinkingRanges = splitIntoRanges();

		// return shrinkingRanges.stream()
		// 					  .flatMap(range -> shrinkIterationsRange(range.get1(), range.get2()));
		return IntStream.range(0, iterations.size())
						.boxed()
						.flatMap(indexToShrink -> shrinkIteration(indexToShrink));
	}

	// private Stream<Shrinkable<Chain<T>>> shrinkIterationsRange(int startIndex, int endIndex) {
	// 	Iteration<T> iterationToShrink = iterations.get(indexToShrink);
	// 	Shrinkable<Chains.Mutator<T>> toShrink = iterationToShrink.shrinkable;
	//
	// 	return toShrink.shrink().map(shrunk -> {
	// 		List<Iteration<T>> shrunkIterations = iterations.stream().limit(indexToShrink)
	// 														.collect(Collectors.toList());
	// 		Iteration<T> shrunkIteration = new Iteration<>(iterationToShrink.randomSeed, iterationToShrink.stateHasBeenAccessed, shrunk);
	// 		shrunkIterations.add(shrunkIteration);
	// 		// A shrunk chain can never have more iterations than the unshrunk one
	// 		return newShrinkableChain(shrunkIterations, iterations.size());
	// 	});
	// }

	// private Stream<Shrinkable<Chain<T>>> shrinkSequenceOfIterations(List<Iteration<T>> iterations) {
	// 	return new ComprehensiveSizeOfListShrinker()
	// 		.shrink(iterations, 1)
	// 		.map(this::createShrinkableActionSequence);
	// }
	//
	// private Stream<Shrinkable<Chains.Mutator<T>>> shrinkIterationsOneAfterTheOther() {
	// 	List<Shrinkable<Action<T>>> shrinkableActions = actionGenerator.generated();
	// 	List<Stream<Shrinkable<ActionSequence<T>>>> shrinkPerElementStreams = new ArrayList<>();
	// 	for (int i = 0; i < shrinkableActions.size(); i++) {
	// 		int index = i;
	// 		Shrinkable<Action<T>> element = shrinkableActions.get(i);
	// 		Stream<Shrinkable<ActionSequence<T>>> shrinkElement = element.shrink().flatMap(shrunkElement -> {
	// 			List<Shrinkable<Action<T>>> actionsCopy = new ArrayList<>(shrinkableActions);
	// 			actionsCopy.set(index, shrunkElement);
	// 			return Stream.of(createShrinkableActionSequence(actionsCopy));
	// 		});
	// 		shrinkPerElementStreams.add(shrinkElement);
	// 	}
	// 	return JqwikStreamSupport.concat(shrinkPerElementStreams);
	// }


	// A shrinking range comprise
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

	private Stream<Shrinkable<Chain<T>>> shrinkIteration(int indexToShrink) {
		Iteration<T> iterationToShrink = iterations.get(indexToShrink);
		Shrinkable<Chains.Mutator<T>> toShrink = iterationToShrink.shrinkable;

		return toShrink.shrink().map(shrunk -> {
			List<Iteration<T>> shrunkIterations = iterations.stream().limit(indexToShrink)
															.collect(Collectors.toList());
			Iteration<T> shrunkIteration = new Iteration<>(iterationToShrink.randomSeed, iterationToShrink.stateHasBeenAccessed, shrunk);
			shrunkIterations.add(shrunkIteration);
			// A shrunk chain can never have more iterations than the unshrunk one
			return newShrinkableChain(shrunkIterations, iterations.size());
		});
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

			// Create deterministic random in order to be able to compare on rerun
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
			Shrinkable<Chains.Mutator<T>> next;
			Iteration<T> iteration = iterations.get(steps);
			if (nextSeed == iteration.randomSeed) {
				next = iteration.shrinkable;
			} else {
				// TODO: Maybe this should delete rest of save iterations.
				throw new RuntimeException("Generation mismatch while rerunning chain");
			}
			return next;
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
