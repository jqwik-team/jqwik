package net.jqwik.engine.execution;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;

public class AfterFailureParametersGenerator implements ParametersGenerator {

	private static final Logger LOG = Logger.getLogger(AfterFailureParametersGenerator.class.getName());

	private final AfterFailureMode afterFailureMode;
	private final GenerationInfo previousFailureGeneration;
	private final ParametersGenerator parametersGenerator;

	private boolean continueWithSeed = false;
	private boolean runWithPreviousSample = false;

	public AfterFailureParametersGenerator(
		AfterFailureMode afterFailureMode,
		GenerationInfo previousFailureGeneration,
		ParametersGenerator parametersGenerator
	) {
		logAfterFailureHandling(afterFailureMode, previousFailureGeneration);
		initializeRunningState(afterFailureMode);
		this.afterFailureMode = afterFailureMode;
		this.previousFailureGeneration = previousFailureGeneration;
		this.parametersGenerator = parametersGenerator;
	}

	private void initializeRunningState(AfterFailureMode afterFailureMode) {
		if (afterFailureMode == AfterFailureMode.SAMPLE_FIRST || afterFailureMode == AfterFailureMode.PREVIOUS_SEED) {
			this.continueWithSeed = true;
		}
		if (afterFailureMode == AfterFailureMode.SAMPLE_FIRST || afterFailureMode == AfterFailureMode.SAMPLE_ONLY) {
			this.runWithPreviousSample = true;
		}
	}

	private void logAfterFailureHandling(AfterFailureMode afterFailureMode, GenerationInfo previousFailureGeneration) {
		String message = String.format("After Failure Handling: %s, Previous Generation: <%s>", afterFailureMode, previousFailureGeneration);
		LOG.log(Level.INFO, message);
	}

	@Override
	public boolean hasNext() {
		if (runWithPreviousSample) {
			return true;
		}
		if (continueWithSeed) {
			return parametersGenerator.hasNext();
		}
		return false;
	}

	@Override
	public Tuple2<TryLifecycleContext, List<Shrinkable<Object>>> next(Supplier<TryLifecycleContext> contextSupplier) {
		if (runWithPreviousSample) {
			Tuple2<TryLifecycleContext, List<Shrinkable<Object>>> previousSample = generatePreviousSample(contextSupplier);
			runWithPreviousSample = false;
			if (previousSample == null) {
				String message = String.format("Cannot generated previous falsified sample <%s>.", previousFailureGeneration);
				// LOG.warning(message);
				throw new JqwikException(message);
			}
			return previousSample;
		}
		if (continueWithSeed) {
			return parametersGenerator.next(contextSupplier);
		}
		return null;
	}

	private Tuple2<TryLifecycleContext, List<Shrinkable<Object>>> generatePreviousSample(Supplier<TryLifecycleContext> context) {
		return null;
	}

	@Override
	public int edgeCasesTotal() {
		return parametersGenerator.edgeCasesTotal();
	}

	@Override
	public int edgeCasesTried() {
		return parametersGenerator.edgeCasesTried();
	}

	@Override
	public int generationIndex() {
		return parametersGenerator.generationIndex();
	}
}
