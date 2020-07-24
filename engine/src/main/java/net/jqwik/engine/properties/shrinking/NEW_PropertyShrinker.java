package net.jqwik.engine.properties.shrinking;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.logging.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

import static java.util.Arrays.*;

public class NEW_PropertyShrinker {

	private static final Logger LOG = Logger.getLogger(NEW_PropertyShrinker.class.getName());

	private final static int BOUNDED_SHRINK_STEPS = 1000;

	private final FalsifiedSample originalSample;
	private final ShrinkingMode shrinkingMode;
	private final Consumer<List<Object>> falsifiedSampleReporter;
	private final Method targetMethod;

	public NEW_PropertyShrinker(
		FalsifiedSample originalSample,
		ShrinkingMode shrinkingMode,
		Consumer<List<Object>> falsifiedSampleReporter,
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

		AtomicInteger shrinkingStepsCounter = new AtomicInteger(0);
		FalsifiedSample shrunkSample = shrinkOneParameterAfterTheOther(forAllFalsifier, originalSample, shrinkingStepsCounter);
		return new ShrunkFalsifiedSample(shrunkSample, shrinkingStepsCounter.get());
	}

	private FalsifiedSample shrinkOneParameterAfterTheOther(
		Falsifier<List<Object>> falsifier,
		FalsifiedSample sample,
		AtomicInteger shrinkingStepsCounter
	) {
		return new NEW_OneAfterTheOtherShrinker().shrink(falsifier, sample, shrinkingStepsCounter);
	}

	private ShrunkFalsifiedSample unshrunkOriginalSample() {
		return new ShrunkFalsifiedSample(originalSample, 0);
	}

	private boolean isFalsifiedButErrorIsNotEquivalent(TryExecutionResult result, Optional<Throwable> originalError) {
		boolean areEquivalent = new NEW_ErrorEquivalenceChecker(targetMethod).areEquivalent(originalError, result.throwable());
		return result.isFalsified() && !areEquivalent;
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
