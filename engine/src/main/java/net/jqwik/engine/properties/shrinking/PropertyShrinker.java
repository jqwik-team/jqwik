package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class PropertyShrinker {

	private final static int BOUNDED_SHRINK_STEPS = 1000;

	private final List<Shrinkable<Object>> shrinkableParameters;
	private final ShrinkingMode shrinkingMode;
	private final Reporter reporter;
	private final Consumer<List<Object>> falsifiedSampleReporter;

	public PropertyShrinker(
		List<Shrinkable<Object>> shrinkableParameters,
		ShrinkingMode shrinkingMode,
		Reporter reporter,
		Consumer<List<Object>> falsifiedSampleReporter
	) {
		this.shrinkableParameters = shrinkableParameters;
		this.shrinkingMode = shrinkingMode;
		this.reporter = reporter;
		this.falsifiedSampleReporter = falsifiedSampleReporter;
	}

	public PropertyShrinkingResult shrink(Falsifier<List<Object>> forAllFalsifier, Throwable originalError) {
		List<Object> originalParameters = toValues(shrinkableParameters);

		if (shrinkingMode == ShrinkingMode.OFF) {
			return new PropertyShrinkingResult(originalParameters, 0, originalError);
		}

		Falsifier<List<Object>> allowOnlyEquivalentErrorsFalsifier = sample -> {
			TryExecutionResult result = forAllFalsifier.execute(sample);
			if (isFalsifiedButErrorIsNotEquivalent(result, originalError)) {
				return TryExecutionResult.invalid();
			}
			return result;
		};

		Function<List<Shrinkable<Object>>, ShrinkingDistance> distanceFunction = ShrinkingDistance::combine;
		ShrinkingSequence<List<Object>> sequence = new ShrinkElementsSequence<>(shrinkableParameters, allowOnlyEquivalentErrorsFalsifier, distanceFunction);
		sequence.init(FalsificationResult.falsified(Shrinkable.unshrinkable(originalParameters), originalError));

		Consumer<FalsificationResult<List<Object>>> falsifiedReporter = result -> falsifiedSampleReporter.accept(result.value());

		AtomicInteger shrinkingStepsCounter = new AtomicInteger(0);
		while (sequence.next(shrinkingStepsCounter::incrementAndGet, falsifiedReporter)) {
			if (shrinkingMode == ShrinkingMode.BOUNDED && shrinkingStepsCounter.get() >= BOUNDED_SHRINK_STEPS) {
				reportShrinkingBoundReached(shrinkingStepsCounter.get());
				break;
			}
		}

		if (shrinkingStepsCounter.get() == 0 && sequence.current().value().equals(originalParameters)) {
			return new PropertyShrinkingResult(originalParameters, 0, originalError);
		}

		return createShrinkingResult(forAllFalsifier, sequence.current(), shrinkingStepsCounter.get());
	}

	private boolean isFalsifiedButErrorIsNotEquivalent(TryExecutionResult result, Throwable originalError) {
		Throwable currentError = result.throwable().orElse(null);
		return result.isFalsified() && !areEquivalent(originalError, currentError);
	}

	/**
	 * Equivalence of falsified property:
	 * - Either both exceptions are null
	 * - Or both exceptions have the same type and their stack trace ended in same location
	 */
	private boolean areEquivalent(Throwable originalError, Throwable currentError) {
		if (originalError == null) {
			return currentError == null;
		}
		if (currentError == null) {
			return false;
		}
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

	private PropertyShrinkingResult createShrinkingResult(
		final Falsifier<List<Object>> forAllFalsifier,
		final FalsificationResult<List<Object>> current,
		final int steps
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
		if (areEquivalent(capturingResult.throwable().orElse(null), current.throwable().orElse(null))) {
			return new PropertyShrinkingResult(sampleCapture[0], steps, capturingResult.throwable().orElse(null));
		} else {
			return new PropertyShrinkingResult(current.value(), steps, current.throwable().orElse(null));
		}
	}

	private List<Object> toValues(List<Shrinkable<Object>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	private void reportShrinkingBoundReached(int steps) {
		String value = String.format(
			"after %s steps." +
				"%n  You can switch on full shrinking with '@Property(shrinking = ShrinkingMode.FULL)'",
			steps
		);
		reporter.publishValue("shrinking bound reached", value);
	}

}
