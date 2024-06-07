package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.engine.support.*;

import org.jspecify.annotations.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.*;

public class ShrinkableChain<T extends @Nullable Object> implements Shrinkable<Chain<T>> {

	public static final int MAX_TRANSFORMER_TRIES = 1000;
	private final long randomSeed;
	private final Supplier<? extends T> initialSupplier;
	private final Function<? super Random, ? extends Transformation<T>> transformationGenerator;
	private final int maxTransformations;
	private final int genSize;
	private final List<ShrinkableChainIteration<T>> iterations;
	private final Supplier<? extends ChangeDetector<? super T>> changeDetectorSupplier;

	public ShrinkableChain(
		long randomSeed,
		Supplier<? extends T> initialSupplier,
		Function<? super Random, ? extends Transformation<T>> transformationGenerator,
		Supplier<? extends ChangeDetector<? super T>> changeDetectorSupplier,
		int maxTransformations,
		int genSize
	) {
		this(randomSeed, initialSupplier, transformationGenerator, changeDetectorSupplier, maxTransformations, genSize, new ArrayList<>());
	}

	private ShrinkableChain(
		long randomSeed, Supplier<? extends T> initialSupplier,
		Function<? super Random, ? extends Transformation<T>> transformationGenerator,
		Supplier<? extends ChangeDetector<? super T>> changeDetectorSupplier,
		int maxTransformations,
		int genSize,
		List<ShrinkableChainIteration<T>> iterations
	) {
		this.randomSeed = randomSeed;
		this.initialSupplier = initialSupplier;
		this.transformationGenerator = transformationGenerator;
		this.changeDetectorSupplier = changeDetectorSupplier;
		this.maxTransformations = maxTransformations;
		this.genSize = genSize;
		this.iterations = iterations;
	}

	@Override
	public Chain<T> value() {
		return new ChainInstance();
	}

	@Override
	public Stream<Shrinkable<Chain<T>>> shrink() {
		return new ShrinkableChainShrinker<>(this, iterations, maxTransformations).shrink();
	}

	ShrinkableChain<T> cloneWith(List<ShrinkableChainIteration<T>> shrunkIterations, int newMaxSize) {
		return new ShrinkableChain<>(
			randomSeed,
			initialSupplier,
			transformationGenerator,
			changeDetectorSupplier,
			newMaxSize,
			genSize,
			shrunkIterations
		);
	}

	@Override
	public ShrinkingDistance distance() {
		List<Shrinkable<Transformer<T>>> shrinkablesForDistance = new ArrayList<>();
		for (int i = 0; i < maxTransformations; i++) {
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
		return String.format("ShrinkableChain[maxSize=%s, iterations=%s]", maxTransformations, iterations);
	}

	private class ChainInstance implements Chain<T> {

		@Override
		public Iterator<T> start() {
			return new ChainIterator(initialSupplier.get());
		}

		@Override
		public int maxTransformations() {
			return maxTransformations;
		}

		@Override
		public List<String> transformations() {
			return iterations.stream().map(i -> i.transformation()).collect(Collectors.toList());
		}

		@Override
		public List<Transformer<T>> transformers() {
			return iterations.stream().map(i -> i.transformer()).collect(Collectors.toList());
		}

		@Override
		public String toString() {
			String actionsString = JqwikStringSupport.displayString(transformations());
			return String.format("Chain: %s", actionsString);
		}
	}

	private class ChainIterator implements Iterator<T> {

		private final Random random = SourceOfRandomness.newRandom(randomSeed);
		private int steps = 0;
		private @Nullable T current;
		private boolean initialSupplied = false;
		private @Nullable Transformer<T> nextTransformer = null;

		private ChainIterator(T initial) {
			this.current = initial;
		}

		@Override
		public boolean hasNext() {
			if (!initialSupplied) {
				return true;
			}
			synchronized (ShrinkableChain.this) {
				if (isInfinite()) {
					nextTransformer = nextTransformer();
					return !nextTransformer.isEndOfChain();
				} else {
					if (steps < maxTransformations) {
						nextTransformer = nextTransformer();
						return !nextTransformer.isEndOfChain();
					} else {
						return false;
					}
				}
			}
		}

		@Override
		public T next() {
			if (!initialSupplied) {
				initialSupplied = true;
				return current;
			}

			synchronized (ShrinkableChain.this) {
				Transformer<T> transformer = nextTransformer;
				current = transformState(transformer, current);
				return current;
			}
		}

		private Transformer<T> nextTransformer() {
			// Fix random seed for same random sequence in re-runs
			long nextSeed = random.nextLong();

			if (steps < iterations.size()) {
				return rerunStep();
			} else {
				return runNewStep(nextSeed);
			}
		}

		private T transformState(Transformer<T> transformer, T before) {
			ChangeDetector<? super T> changeDetector = changeDetectorSupplier.get();
			changeDetector.before(before);
			try {
				T after = transformer.apply(before);
				boolean stateHasChanged = changeDetector.hasChanged(after);
				ShrinkableChainIteration<T> currentIteration = iterations.get(steps);
				iterations.set(steps, currentIteration.withStateChange(stateHasChanged));
				return after;
			} finally {
				steps++;
			}
		}

		private Transformer<T> rerunStep() {
			ShrinkableChainIteration<T> iteration = iterations.get(steps);
			iteration.precondition().ifPresent(predicate -> {
				if (!predicate.test(current)) {
					throw new TestAbortedException("Precondition no longer valid");
				}
			});
			// TODO: Could that be optimized to iteration.transformer()?
			return iteration.shrinkable.value();
		}

		private Transformer<T> runNewStep(long nextSeed) {
			Random random = SourceOfRandomness.newRandom(nextSeed);

			AtomicInteger attemptsCounter = new AtomicInteger(0);
			while (attemptsCounter.get() < MAX_TRANSFORMER_TRIES) {
				Tuple3<Arbitrary<Transformer<T>>, Predicate<T>, Boolean> arbitraryAccessTuple = nextTransformerArbitrary(random, attemptsCounter);
				Arbitrary<Transformer<T>> arbitrary = arbitraryAccessTuple.get1();
				Predicate<T> precondition = arbitraryAccessTuple.get2();
				boolean accessState = arbitraryAccessTuple.get3();

				RandomGenerator<Transformer<T>> generator = arbitrary.generator(genSize);
				Shrinkable<Transformer<T>> nextShrinkable = generator.next(random);
				Transformer<T> next = nextShrinkable.value();
				if (next == Transformer.noop()) {
					continue;
				}
				iterations.add(new ShrinkableChainIteration<>(precondition, accessState, nextShrinkable));
				return next;
			}
			return failWithTooManyAttempts(attemptsCounter);
		}

		private Tuple3<Arbitrary<Transformer<T>>, Predicate<T>, Boolean> nextTransformerArbitrary(
			Random random,
			AtomicInteger attemptsCounter
		) {
			AtomicBoolean accessState = new AtomicBoolean(false);
			Supplier<T> supplier = () -> {
				accessState.set(true);
				return current;
			};

			while (attemptsCounter.getAndIncrement() < MAX_TRANSFORMER_TRIES) {
				Transformation<T> chainGenerator = transformationGenerator.apply(random);

				Predicate<T> precondition = chainGenerator.precondition();
				boolean hasPrecondition = precondition != Transformation.NO_PRECONDITION;
				if (hasPrecondition && !precondition.test(current)) {
					continue;
				}

				accessState.set(false);
				Arbitrary<Transformer<T>> arbitrary = chainGenerator.apply(supplier);
				return Tuple.of(arbitrary, hasPrecondition ? precondition : null, accessState.get());
			}
			return failWithTooManyAttempts(attemptsCounter);
		}

		private <R> R failWithTooManyAttempts(AtomicInteger attemptsCounter) {
			String message = String.format("Could not generate a transformer after %s attempts.", attemptsCounter.get());
			throw new JqwikException(message);
		}
	}

	private boolean isInfinite() {
		return maxTransformations < 0;
	}

}
