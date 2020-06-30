package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class PropertyShrinker {

	private final static int BOUNDED_SHRINK_STEPS = 1000;

	private final List<Shrinkable<Object>> parameters;
	private final ShrinkingMode shrinkingMode;
	private final Reporter reporter;
	private final Consumer<List<Object>> falsifiedSampleReporter;

	public PropertyShrinker(
		List<Shrinkable<Object>> parameters,
		ShrinkingMode shrinkingMode,
		Reporter reporter,
		Consumer<List<Object>> falsifiedSampleReporter
	) {
		this.parameters = parameters;
		this.shrinkingMode = shrinkingMode;
		this.reporter = reporter;
		this.falsifiedSampleReporter = falsifiedSampleReporter;
	}

	public PropertyShrinkingResult shrink(Falsifier<List<Object>> forAllFalsifier, Throwable originalError) {
		if (shrinkingMode == ShrinkingMode.OFF) {
			return new PropertyShrinkingResult(toValues(parameters), 0, originalError);
		}

		Function<List<Shrinkable<Object>>, ShrinkingDistance> distanceFunction = ShrinkingDistance::combine;
		ShrinkingSequence<List<Object>> sequence = new ShrinkElementsSequence<>(parameters, forAllFalsifier, distanceFunction);
		sequence.init(FalsificationResult.falsified(Shrinkable.unshrinkable(toValues(parameters)), originalError));

		Consumer<FalsificationResult<List<Object>>> falsifiedReporter = result -> falsifiedSampleReporter.accept(result.value());

		AtomicInteger shrinkingStepsCounter = new AtomicInteger(0);
		while (sequence.next(shrinkingStepsCounter::incrementAndGet, falsifiedReporter)) {
			if (shrinkingMode == ShrinkingMode.BOUNDED && shrinkingStepsCounter.get() >= BOUNDED_SHRINK_STEPS) {
				reportShrinkingBoundReached(shrinkingStepsCounter.get());
				break;
			}
		}

		if (shrinkingStepsCounter.get() == 0) {
			return new PropertyShrinkingResult(toValues(parameters), 0, originalError);
		}

		return createShrinkingResult(forAllFalsifier, sequence.current(), shrinkingStepsCounter.get());
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

		return new PropertyShrinkingResult(sampleCapture[0], steps, capturingResult.throwable().orElse(null));
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
