package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

public class PropertyShrinker {

	private static final Logger LOG = Logger.getLogger(PropertyShrinker.class.getName());

	private final static int BOUNDED_SHRINK_STEPS = 1000;

	private final FalsifiedSample originalSample;
	private final ShrinkingMode shrinkingMode;
	private final Consumer<List<Object>> falsifiedSampleReporter;

	public PropertyShrinker(
		FalsifiedSample originalSample,
		ShrinkingMode shrinkingMode,
		Consumer<List<Object>> falsifiedSampleReporter
	) {
		this.originalSample = originalSample;
		this.shrinkingMode = shrinkingMode;
		this.falsifiedSampleReporter = falsifiedSampleReporter;
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

		Function<List<Shrinkable<Object>>, ShrinkingDistance> distanceFunction = ShrinkingDistance::combine;
		ShrinkElementsSequence<Object> sequence =
			new ShrinkElementsSequence<>(originalSample.shrinkables(), allowOnlyEquivalentErrorsFalsifier, distanceFunction);
		sequence.init(FalsificationResult.falsified(
			Shrinkable.unshrinkable(originalSample.parameters()),
			originalSample.falsifyingError().orElse(null)
		));

		Consumer<FalsificationResult<List<Object>>> falsifiedReporter = result -> falsifiedSampleReporter.accept(result.value());

		AtomicInteger shrinkingStepsCounter = new AtomicInteger(0);
		while (sequence.next(shrinkingStepsCounter::incrementAndGet, falsifiedReporter)) {
			if (shrinkingMode == ShrinkingMode.BOUNDED && shrinkingStepsCounter.get() >= BOUNDED_SHRINK_STEPS) {
				logShrinkingBoundReached(shrinkingStepsCounter.get());
				break;
			}
		}

		if (shrinkingStepsCounter.get() == 0 && sequence.current().value().equals(originalSample.parameters())) {
			return unshrunkOriginalSample();
		}

		return createShrinkingResult(forAllFalsifier, sequence.current(), shrinkingStepsCounter.get());
	}

	public ShrunkFalsifiedSample unshrunkOriginalSample() {
		return new ShrunkFalsifiedSample(
			originalSample.parameters(),
			originalSample.shrinkables(),
			originalSample.falsifyingError(),
			0
		);
	}

	private boolean isFalsifiedButErrorIsNotEquivalent(TryExecutionResult result, Optional<Throwable> originalError) {
		return result.isFalsified() && !areEquivalent(originalError, result.throwable());
	}

	/**
	 * Equivalence of falsified property:
	 * - Either both exceptions are null
	 * - Or both exceptions have the same type and their stack trace ended in same location
	 */
	private boolean areEquivalent(Optional<Throwable> optionalOriginal, Optional<Throwable> optionalCurrent) {
		if (!optionalOriginal.isPresent()) {
			return !optionalCurrent.isPresent();
		}
		if (!optionalCurrent.isPresent()) {
			return false;
		}
		Throwable originalError = optionalOriginal.get();
		Throwable currentError = optionalCurrent.get();
		if (!originalError.getClass().equals(currentError.getClass())) {
			return false;
		}
		Optional<StackTraceElement> firstOriginal = firstStackTraceElement(originalError);
		Optional<StackTraceElement> firstCurrent = firstStackTraceElement(currentError);
		return firstOriginal.equals(firstCurrent);
	}

	private Optional<StackTraceElement> firstStackTraceElement(Throwable error) {
		StackTraceElement[] stackTrace = error.getStackTrace();
		if (stackTrace.length == 0) {
			return Optional.empty();
		}
		return Optional.of(stackTrace[0]);
	}

	private ShrunkFalsifiedSample createShrinkingResult(
		Falsifier<List<Object>> forAllFalsifier,
		FalsificationResult<List<Object>> current,
		int steps
	) {
		// TODO: Remove this hack by a new decent implementation of shrinking

		// Capture the real sample not just what the final shrinking result shrinkable generates
		@SuppressWarnings("unchecked")
		List<Object>[] sampleCapture = new List[1];
		Falsifier<List<Object>> captureSampleFalsifier = sample -> {
			sampleCapture[0] = sample;
			return forAllFalsifier.execute(sample);
		};
		FalsificationResult<List<Object>> capturingResult = captureSampleFalsifier.falsify(current.shrinkable());

		// Sometimes, in the context of mutable objects the capturing result is not equivalent to the shrunk result
		// but this is all terrible hack for the drawbacks of current shrinking implementation
		List<Shrinkable<Object>> shrinkables = current.shrinkable().value().stream().map(Shrinkable::unshrinkable)
													  .collect(Collectors.toList());
		if (areEquivalent(capturingResult.throwable(), current.throwable())) {
			return new ShrunkFalsifiedSample(sampleCapture[0], shrinkables, capturingResult.throwable(), steps);
		} else {
			return new ShrunkFalsifiedSample(current.value(), shrinkables, current.throwable(), steps);
		}
	}

	private void logShrinkingBoundReached(int steps) {
		String value = String.format(
			"Shrinking bound reached after %s steps." +
				"%n  You can switch on full shrinking with '@Property(shrinking = ShrinkingMode.FULL)'",
			steps
		);
		LOG.warning(value);
	}

}
