package net.jqwik.engine.properties.shrinking;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

public class PropertyShrinker {

	private static final Logger LOG = Logger.getLogger(PropertyShrinker.class.getName());

	private final FalsifiedSample originalSample;
	private final ShrinkingMode shrinkingMode;
	private final int boundedShrinkingSeconds;
	private final Consumer<FalsifiedSample> falsifiedSampleReporter;
	private final Method targetMethod;

	private final AtomicInteger shrinkingStepsCounter = new AtomicInteger(0);

	private final List<TryExecutionResult.Status> shrinkingSequence = new LinkedList<>();

	private Optional<FalsifiedSample> currentBest = Optional.empty();

	public PropertyShrinker(
		FalsifiedSample originalSample,
		ShrinkingMode shrinkingMode,
		int boundedShrinkingSeconds,
		Consumer<FalsifiedSample> falsifiedSampleReporter,
		Method targetMethod
	) {
		this.originalSample = originalSample;
		this.shrinkingMode = shrinkingMode;
		this.boundedShrinkingSeconds = boundedShrinkingSeconds;
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
			if (currentBest != null) {
				this.currentBest = Optional.of(currentBest);
			}
		};

		return shrink(allowOnlyEquivalentErrorsFalsifier, shrinkSampleConsumer, shrinkAttemptConsumer);
	}

	public List<TryExecutionResult.Status> shrinkingSequence() {
		int indexOfLastFalsified = shrinkingSequence.lastIndexOf(TryExecutionResult.Status.FALSIFIED);
		return shrinkingSequence.subList(0, indexOfLastFalsified + 1);
	}

	private ShrunkFalsifiedSample shrink(
		Falsifier<List<Object>> falsifier,
		Consumer<FalsifiedSample> shrinkSampleConsumer,
		Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		FalsifiedSample fullyShrunkSample;
		Supplier<FalsifiedSample> shrinkUntilDone = () -> shrinkAsLongAsSampleImproves(falsifier, shrinkSampleConsumer, shrinkAttemptConsumer);
		if (shrinkingMode == ShrinkingMode.FULL) {
			fullyShrunkSample = shrinkUntilDone.get();
		} else {
			fullyShrunkSample = withTimeout(shrinkUntilDone);
		}
		return new ShrunkFalsifiedSampleImpl(fullyShrunkSample, shrinkingStepsCounter.get());
	}

	private FalsifiedSample withTimeout(Supplier<FalsifiedSample> shrinkUntilDone) {
		try {
			TestDescriptor current = CurrentTestDescriptor.get();
			Supplier<FalsifiedSample> shrinkWithTestDescriptor = () -> CurrentTestDescriptor.runWithDescriptor(current, shrinkUntilDone);
			CompletableFuture<FalsifiedSample> falsifiedSampleFuture = CompletableFuture.supplyAsync(shrinkWithTestDescriptor);
			return falsifiedSampleFuture.get(boundedShrinkingSeconds, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException e) {
			return JqwikExceptionSupport.throwAsUncheckedException(e);
		} catch (TimeoutException e) {
			logShrinkingBoundReached();
			return currentBest.orElse(originalSample);
		}
	}

	private FalsifiedSample shrinkAsLongAsSampleImproves(
		final Falsifier<List<Object>> falsifier,
		final Consumer<FalsifiedSample> shrinkSampleConsumer,
		final Consumer<FalsifiedSample> shrinkAttemptConsumer
	) {
		Falsifier<List<Object>> recordingFalsifier = params -> {
			TryExecutionResult executionResult = falsifier.execute(params);
			shrinkingSequence.add(executionResult.status());
			return executionResult;
		};

		PlainShrinker plainShrinker = new PlainShrinker(
			originalSample,
			shrinkSampleConsumer,
			shrinkAttemptConsumer
		);

		return plainShrinker.shrink(recordingFalsifier);
	}

	private ShrunkFalsifiedSample unshrunkOriginalSample() {
		return new ShrunkFalsifiedSampleImpl(originalSample, 0);
	}

	private boolean isFalsifiedButErrorIsNotEquivalent(TryExecutionResult result, Optional<Throwable> originalError) {
		boolean areEquivalent = new ErrorEquivalenceChecker(targetMethod).areEquivalent(originalError, result.throwable());
		return result.isFalsified() && !areEquivalent;
	}

	private void logShrinkingBoundReached() {
		String value = String.format(
			"Shrinking timeout reached after %s seconds." +
				"%n  You can switch on full shrinking with '@Property(shrinking = ShrinkingMode.FULL)'",
			boundedShrinkingSeconds
		);
		LOG.warning(value);
	}

}
