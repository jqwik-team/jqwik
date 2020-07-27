package net.jqwik.engine.properties.shrinking;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.logging.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

public class NEW_PropertyShrinker {

	private static final Logger LOG = Logger.getLogger(NEW_PropertyShrinker.class.getName());

	private final static int BOUNDED_SHRINK_ATTEMPTS = 10000;

	private final FalsifiedSample originalSample;
	private final ShrinkingMode shrinkingMode;
	private final Consumer<FalsifiedSample> falsifiedSampleReporter;
	private final Method targetMethod;

	private final AtomicInteger shrinkingStepsCounter = new AtomicInteger(0);
	private final AtomicInteger shrinkingAttemptsCounter = new AtomicInteger(0);

	public NEW_PropertyShrinker(
		FalsifiedSample originalSample,
		ShrinkingMode shrinkingMode,
		Consumer<FalsifiedSample> falsifiedSampleReporter,
		Method targetMethod
	) {
		this.originalSample = originalSample;
		this.shrinkingMode = shrinkingMode;
		this.falsifiedSampleReporter = falsifiedSampleReporter;
		this.targetMethod = targetMethod;
	}

	public ShrunkFalsifiedSample shrink(Falsifier<List<Object>> forAllFalsifier) {
		if (shrinkingMode == ShrinkingMode.OFF) {
			return unshrunkOriginalSample();
		}

		Falsifier<List<Object>> allowOnlyEquivalentErrorsFalsifier = sample -> {
			TryExecutionResult result = forAllFalsifier.execute(sample);
			if (isFalsifiedButErrorIsNotEquivalent(result, originalSample.falsifyingError())) {
				return TryExecutionResult.invalid();
			}
			return result;
		};

		Consumer<FalsifiedSample> shrinkSampleConsumer = sample -> {
			shrinkingStepsCounter.incrementAndGet();
			falsifiedSampleReporter.accept(sample);
		};

		Consumer<FalsifiedSample> shrinkAttemptConsumer = currentBest -> {
			int numberOfAttempts = shrinkingAttemptsCounter.getAndIncrement();
			if (shrinkingMode == ShrinkingMode.BOUNDED && numberOfAttempts >= BOUNDED_SHRINK_ATTEMPTS) {
				throw new ShrinkingBoundReached(numberOfAttempts, currentBest);
			}
		};

		return shrink(allowOnlyEquivalentErrorsFalsifier, shrinkSampleConsumer, shrinkAttemptConsumer);
	}

	public ShrunkFalsifiedSample shrink(
		Falsifier<List<Object>> falsifier,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		try {
			FalsifiedSample fullyShrunkSample = shrinkAsLongAsSampleImproves(falsifier, shrinkSampleConsumer, shrinkAttemptConsumer);
			return new ShrunkFalsifiedSample(fullyShrunkSample, shrinkingStepsCounter.get());
		} catch (ShrinkingBoundReached shrinkingBoundReached) {
			logShrinkingBoundReached(shrinkingBoundReached.numberOfAttempts);
			return new ShrunkFalsifiedSample(shrinkingBoundReached.currentBest.orElse(originalSample), shrinkingStepsCounter.get());
		}
	}

	public FalsifiedSample shrinkAsLongAsSampleImproves(
		final Falsifier<List<Object>> falsifier,
		final Consumer<FalsifiedSample> shrinkSampleConsumer,
		final Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		FalsifiedSample after = originalSample;
		FalsifiedSample before;
		do {
			before = after;
			after = shrinkOneParameterAfterTheOther(falsifier, before, shrinkSampleConsumer, shrinkAttemptConsumer);
		} while (!after.equals(before));
		return before;
	}

	private FalsifiedSample shrinkOneParameterAfterTheOther(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		final Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		return new NEW_OneAfterTheOtherShrinker().shrink(falsifier, sample, shrinkSampleConsumer, shrinkAttemptConsumer);
	}

	private ShrunkFalsifiedSample unshrunkOriginalSample() {
		return new ShrunkFalsifiedSample(originalSample, 0);
	}

	private boolean isFalsifiedButErrorIsNotEquivalent(TryExecutionResult result, Optional<Throwable> originalError) {
		boolean areEquivalent = new NEW_ErrorEquivalenceChecker(targetMethod).areEquivalent(originalError, result.throwable());
		return result.isFalsified() && !areEquivalent;
	}

	private void logShrinkingBoundReached(int attempts) {
		String value = String.format(
			"Shrinking bound reached after %s attempts." +
				"%n  You can switch on full shrinking with '@Property(shrinking = ShrinkingMode.FULL)'",
			attempts
		);
		LOG.warning(value);
	}

	private class ShrinkingBoundReached extends RuntimeException {
		private final int numberOfAttempts;
		private final Optional<FalsifiedSample> currentBest;

		private ShrinkingBoundReached(int numberOfAttempts, FalsifiedSample currentBest) {
			super("Shrinking attempts bound reached");
			this.numberOfAttempts = numberOfAttempts;
			this.currentBest = Optional.ofNullable(currentBest);
		}
	}
}
