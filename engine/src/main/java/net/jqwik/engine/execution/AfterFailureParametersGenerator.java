package net.jqwik.engine.execution;

import java.util.*;
import java.util.logging.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class AfterFailureParametersGenerator implements ParametersGenerator {

	private static final Logger LOG = Logger.getLogger(AfterFailureParametersGenerator.class.getName());

	private final AfterFailureMode afterFailureMode;
	private final GenerationInfo previousFailureGeneration;
	private final ParametersGenerator parametersGenerator;

	private boolean continueWithSeed = false;
	private boolean runWithPreviousSample = false;
	private boolean previousSampleHasJustRun = false;

	public AfterFailureParametersGenerator(
		AfterFailureMode afterFailureMode,
		GenerationInfo previousFailureGeneration,
		ParametersGenerator parametersGenerator
	) {
		logAfterFailureHandling(afterFailureMode, previousFailureGeneration);
		initializeRunningState(afterFailureMode, previousFailureGeneration);
		this.afterFailureMode = afterFailureMode;
		this.previousFailureGeneration = previousFailureGeneration;
		this.parametersGenerator = parametersGenerator;
	}

	private void initializeRunningState(
		AfterFailureMode afterFailureMode,
		GenerationInfo previousFailureGeneration
	) {
		if (previousFailureGeneration.generationIndex() > 0
				&& (afterFailureMode == AfterFailureMode.SAMPLE_FIRST || afterFailureMode == AfterFailureMode.SAMPLE_ONLY)) {
			this.runWithPreviousSample = true;
		}
		if (afterFailureMode == AfterFailureMode.SAMPLE_FIRST || afterFailureMode == AfterFailureMode.PREVIOUS_SEED) {
			this.continueWithSeed = true;
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
	public List<Shrinkable<Object>> next(TryLifecycleContext context) {
		if (runWithPreviousSample) {
			List<Shrinkable<Object>> previousSample = generatePreviousSample(context);
			runWithPreviousSample = false;
			parametersGenerator.reset();
			if (previousSample == null) {
				String message = String.format(
					"Cannot generate previous falsified sample <%s>.%n" +
						"\tUsing previous seed instead.", previousFailureGeneration
				);
				LOG.warning(message);
				continueWithSeed = true;
				return next(context);
			} else {
				previousSampleHasJustRun = true;
			}
			return previousSample;
		}
		if (continueWithSeed) {
			previousSampleHasJustRun = false;
			return parametersGenerator.next(context);
		}
		return null;
	}

	private List<Shrinkable<Object>> generatePreviousSample(TryLifecycleContext context) {
		List<Shrinkable<Object>> sample = null;
		for (int i = 0; i < previousFailureGeneration.generationIndex(); i++) {
			if (parametersGenerator.hasNext()) {
				sample = parametersGenerator.next(context);
			} else {
				break;
			}
		}
		return sample;
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
	public GenerationInfo generationInfo(String randomSeed) {
		if (previousSampleHasJustRun) {
			return previousFailureGeneration;
		}
		return parametersGenerator.generationInfo(randomSeed);
	}

	@Override
	public void reset() {
		// do nothing
	}
}
