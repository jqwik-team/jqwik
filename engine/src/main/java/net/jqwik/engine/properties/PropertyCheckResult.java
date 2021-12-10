package net.jqwik.engine.properties;

import java.util.*;

import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.execution.reporting.*;

public class PropertyCheckResult implements ExtendedPropertyExecutionResult {

	enum CheckStatus {
		SUCCESSFUL,
		FAILED,
		EXHAUSTED,
		ABORTED
	}

	public static PropertyCheckResult successful(
		String stereotype,
		String propertyName,
		int tries,
		int checks,
		String randomSeed,
		GenerationMode generation,
		EdgeCasesMode edgeCasesMode,
		int edgeCasesTotal,
		int edgeCasesTried
	) {
		return new PropertyCheckResult(
			CheckStatus.SUCCESSFUL, stereotype,
			propertyName,
			tries,
			checks,
			new GenerationInfo(randomSeed),
			generation,
			edgeCasesMode,
			edgeCasesTotal,
			edgeCasesTried,
			null,
			null,
			null
		);
	}

	public static PropertyCheckResult failed(
		String stereotype,
		String propertyName,
		int tries,
		int checks,
		String randomSeed,
		GenerationMode generation,
		EdgeCasesMode edgeCasesMode,
		int edgeCasesTotal,
		int edgeCasesTried,
		FalsifiedSample originalSample,
		ShrunkFalsifiedSample shrunkSample,
		Throwable throwable
	) {
		// If no shrinking was possible, report only original sample
		if (shrunkSample != null && areEquivalent(originalSample, shrunkSample)) {
			shrunkSample = null;
		}
		return new PropertyCheckResult(
			CheckStatus.FAILED,
			stereotype,
			propertyName,
			tries,
			checks,
			new GenerationInfo(randomSeed),
			generation,
			edgeCasesMode,
			edgeCasesTotal,
			edgeCasesTried,
			originalSample,
			shrunkSample,
			throwable
		);
	}

	private static boolean areEquivalent(FalsifiedSample originalSample, ShrunkFalsifiedSample shrunkSample) {
		return originalSample.equals(shrunkSample) && shrunkSample.countShrinkingSteps() == 0;
	}

	public static PropertyCheckResult skipExample(
		String stereotype,
		String propertyName,
		String randomSeed,
		GenerationMode generation,
		EdgeCasesMode edgeCasesMode,
		int edgeCasesTotal,
		int edgeCasesTried,
		Throwable throwable
	) {
		return new PropertyCheckResult(
			CheckStatus.ABORTED,
			stereotype,
			propertyName,
			1,
			0,
			new GenerationInfo(randomSeed),
			generation,
			edgeCasesMode,
			edgeCasesTotal,
			edgeCasesTried,
			null,
			null,
			throwable
		);
	}

	public static PropertyCheckResult exhausted(
		String stereotype,
		String propertyName,
		int tries,
		int checks,
		String randomSeed,
		GenerationMode generation,
		EdgeCasesMode edgeCasesMode,
		int edgeCasesTotal,
		int edgeCasesTried,
		Throwable throwable
	) {
		return new PropertyCheckResult(
			CheckStatus.EXHAUSTED,
			stereotype,
			propertyName,
			tries,
			checks,
			new GenerationInfo(randomSeed),
			generation,
			edgeCasesMode,
			edgeCasesTotal,
			edgeCasesTried,
			null,
			null,
			throwable
		);
	}

	private final String stereotype;
	private final CheckStatus status;
	private final String propertyName;
	private final int tries;
	private final int checks;
	private final GenerationInfo generationInfo;
	private final GenerationMode generation;
	private final EdgeCasesMode edgeCasesMode;
	private final int edgeCasesTotal;
	private final int edgeCasesTried;
	private final FalsifiedSample originalSample;
	private final ShrunkFalsifiedSample shrunkSample;
	private final Throwable throwable;

	private PropertyCheckResult(
		CheckStatus status, String stereotype,
		String propertyName,
		int tries,
		int checks,
		GenerationInfo generationInfo,
		GenerationMode generation,
		EdgeCasesMode edgeCasesMode,
		int edgeCasesTotal,
		int edgeCasesTried,
		FalsifiedSample originalSample,
		ShrunkFalsifiedSample shrunkSample,
		Throwable throwable
	) {
		this.stereotype = stereotype;
		this.status = status;
		this.propertyName = propertyName;
		this.tries = tries;
		this.checks = checks;
		this.generationInfo = generationInfo;
		this.generation = generation;
		this.edgeCasesMode = edgeCasesMode;
		this.edgeCasesTotal = edgeCasesTotal;
		this.edgeCasesTried = edgeCasesTried;
		this.shrunkSample = shrunkSample;
		this.originalSample = originalSample;
		this.throwable = determineThrowable(status, throwable);
	}

	private Throwable determineThrowable(CheckStatus status, Throwable throwable) {
		if (status == CheckStatus.SUCCESSFUL) {
			return null;
		} else {
			return throwable == null
					   ? new AssertionFailedError(this.toString())
					   : throwable;
		}
	}

	@Override
	public Optional<List<Object>> falsifiedParameters() {
		if (shrunkSample != null) {
			return Optional.of(shrunkSample.parameters());
		} else {
			return Optional.ofNullable(originalSample).map(FalsifiedSample::parameters);
		}
	}

	@Override
	public Optional<String> seed() {
		return generationInfo.randomSeed();
	}

	@Override
	public Status status() {
		switch (checkStatus()) {
			case SUCCESSFUL: return Status.SUCCESSFUL;
			case ABORTED: return Status.ABORTED;
			default: return Status.FAILED;
		}
	}

	@Override
	public Optional<Throwable> throwable() {
		return Optional.ofNullable(throwable);
	}

	@Override
	public PropertyExecutionResult mapTo(Status newStatus, Throwable throwable) {
		switch (newStatus) {
			case ABORTED:
				return PlainExecutionResult.aborted(throwable, generationInfo);
			case FAILED:
				return new PropertyCheckResult(
					CheckStatus.FAILED,
					stereotype,
					propertyName,
					tries,
					checks,
					generationInfo,
					generation,
					edgeCasesMode,
					edgeCasesTotal,
					edgeCasesTried,
					originalSample,
					shrunkSample,
					throwable
				);
			case SUCCESSFUL:
				return new PropertyCheckResult(
					CheckStatus.SUCCESSFUL,
					stereotype,
					propertyName,
					tries,
					checks,
					generationInfo,
					generation,
					edgeCasesMode,
					edgeCasesTotal,
					edgeCasesTried,
					null,
					null,
					throwable
				);
			default:
				throw new IllegalStateException(String.format("Unknown state: %s", newStatus.name()));
		}
	}

	@Override
	public boolean isExtended() {
		return true;
	}

	public String propertyName() {
		return propertyName;
	}

	public CheckStatus checkStatus() {
		return status;
	}

	public int countChecks() {
		return checks;
	}

	public int countTries() {
		return tries;
	}

	public GenerationInfo generationInfo() {
		return generationInfo;
	}

	public Optional<FalsifiedSample> originalSample() {
		return Optional.ofNullable(originalSample);
	}

	public Optional<ShrunkFalsifiedSample> shrunkSample() {
		return Optional.ofNullable(shrunkSample);
	}

	public GenerationMode generation() {
		return generation;
	}

	@Override
	public EdgeCasesExecutionResult edgeCases() {
		return new EdgeCasesExecutionResult(edgeCasesMode, edgeCasesTotal, edgeCasesTried);
	}

	@Override
	public String toString() {
		String header = String.format("%s [%s]", stereotype, propertyName);
		switch (checkStatus()) {
			case FAILED:
				String failedMessage = falsifiedParameters().map(sampleParams -> {
					Map<Integer, Object> sampleMap = new HashMap<>();
					for (int i = 0; i < sampleParams.size(); i++) {
						Object parameter = sampleParams.get(i);
						sampleMap.put(i, parameter);
					}
					String sampleString = ValueReport.of(sampleMap).singleLineReport();
					return sampleParams.isEmpty() ? "" : String.format(" with sample %s", sampleString);
				}).orElse("");
				return String.format("%s failed%s", header, failedMessage);
			case EXHAUSTED:
				int rejections = tries - checks;
				return String.format("%s exhausted after [%d] tries and [%d] rejections", header, tries, rejections);
			default:
				return header;
		}
	}

}
