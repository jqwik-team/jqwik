package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.random.*;

import org.jetbrains.annotations.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.*;

public class ShrinkableChain<T> implements Shrinkable<Chain<T>> {

	public static final int MAX_TRANSFORMER_TRIES = 1000;
	private final JqwikRandomState randomSeed;
	private final Supplier<? extends T> initialSupplier;
	private final Function<JqwikRandom, Transformation<T>> transformationGenerator;
	private final int maxTransformations;
	private final int genSize;
	private final List<ShrinkableChainIteration<T>> iterations;
	private final Supplier<ChangeDetector<T>> changeDetectorSupplier;

	public ShrinkableChain(
		JqwikRandomState randomSeed,
		Supplier<? extends T> initialSupplier,
		Function<JqwikRandom, Transformation<T>> transformationGenerator,
		Supplier<ChangeDetector<T>> changeDetectorSupplier,
		int maxTransformations,
		int genSize
	) {
		this(randomSeed, initialSupplier, transformationGenerator, changeDetectorSupplier, maxTransformations, genSize, new ArrayList<>());
	}

	private ShrinkableChain(
		JqwikRandomState randomSeed, Supplier<? extends T> initialSupplier,
		Function<JqwikRandom, Transformation<T>> transformationGenerator,
		Supplier<ChangeDetector<T>> changeDetectorSupplier,
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
	@NotNull
	public Chain<T> value() {
		return new ChainInstance();
	}

	@Override
	@NotNull
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
	@NotNull
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
		@NotNull
		public Iterator<T> start() {
			return new ChainIterator(initialSupplier.get());
		}

		@Override
		public int maxTransformations() {
			return maxTransformations;
		}

		@Override
		@NotNull
		public List<String> transformations() {
			return iterations.stream().map(i -> i.transformation()).collect(Collectors.toList());
		}

		@Override
		@NotNull
		public List<Transformer<T>> transformers() {
			return iterations.stream().map(i -> i.transformer()).collect(Collectors.toList());
		}
	}

	private class ChainIterator implements Iterator<T> {

		private final JqwikRandom random = SourceOfRandomness.newRandom(randomSeed);
		private int steps = 0;
		private T current;
		private boolean initialSupplied = false;
		private Transformer<T> nextTransformer = null;

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
			JqwikRandom nextSeed = random.split();

			Shrinkable<Transformer<T>> next = null;
			if (steps < iterations.size()) {
				next = rerunStep();
			} else {
				next = runNewStep(nextSeed);
			}
			return next.value();
		}

		private T transformState(Transformer<T> transformer, T before) {
			ChangeDetector<T> changeDetector = changeDetectorSupplier.get();
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

		private Shrinkable<Transformer<T>> rerunStep() {
			ShrinkableChainIteration<T> iteration = iterations.get(steps);
			iteration.precondition().ifPresent(predicate -> {
				if (!predicate.test(current)) {
					throw new TestAbortedException("Precondition no longer valid");
				}
			});
			return iteration.shrinkable;
		}

		private Shrinkable<Transformer<T>> runNewStep(JqwikRandom random) {
			AtomicInteger attemptsCounter = new AtomicInteger(0);
			while (attemptsCounter.get() < MAX_TRANSFORMER_TRIES) {
				Tuple3<Arbitrary<Transformer<T>>, Predicate<T>, Boolean> arbitraryAccessTuple = nextTransformerArbitrary(random, attemptsCounter);
				Arbitrary<Transformer<T>> arbitrary = arbitraryAccessTuple.get1();
				Predicate<T> precondition = arbitraryAccessTuple.get2();
				boolean accessState = arbitraryAccessTuple.get3();

				RandomGenerator<Transformer<T>> generator = arbitrary.generator(genSize);
				Shrinkable<Transformer<T>> next = generator.next(random);
				if (next.value() == Transformer.noop()) {
					continue;
				}
				iterations.add(new ShrinkableChainIteration<>(precondition, accessState, next));
				return next;
			}
			return failWithTooManyAttempts(attemptsCounter);
		}

		private Tuple3<Arbitrary<Transformer<T>>, Predicate<T>, Boolean> nextTransformerArbitrary(JqwikRandom random, AtomicInteger attemptsCounter) {
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
