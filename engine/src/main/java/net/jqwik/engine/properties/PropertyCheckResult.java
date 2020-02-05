package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.support.*;

public abstract class PropertyCheckResult implements ExtendedPropertyExecutionResult {

	enum CheckStatus {
		SUCCESSFUL,
		FAILED,
		EXHAUSTED
	}

	public static PropertyCheckResult successful(
		String stereotype,
		String propertyName,
		int tries,
		int checks,
		String randomSeed,
		GenerationMode generation
	) {
		return new PropertyCheckResult(CheckStatus.SUCCESSFUL, propertyName, tries, checks, randomSeed, generation, null, null, null) {
			@Override
			public PropertyExecutionResult changeToSuccessful() {
				return this;
			}

			@Override
			public PropertyExecutionResult changeToFailed(Throwable throwable) {
				return failed(stereotype, propertyName, tries, checks, randomSeed, generation, null, null, throwable);
			}

			@Override
			public String toString() {
				return String.format("%s [%s] satisfied", stereotype, propertyName);
			}
		};
	}

	public static PropertyCheckResult failed(
		String stereotype, String propertyName, int tries, int checks, String randomSeed, GenerationMode generation,
		List<Object> sample, List<Object> originalSample, Throwable throwable
	) {
		return new PropertyCheckResult(CheckStatus.FAILED, propertyName, tries, checks, randomSeed, generation, sample, originalSample, throwable) {
			@Override
			public String toString() {
				String sampleString = sample.isEmpty() ? "" : String.format(" with sample %s", JqwikStringSupport.displayString(sample));
				return String.format("%s [%s] falsified%s", stereotype, propertyName, sampleString);
			}

			@Override
			public PropertyExecutionResult changeToSuccessful() {
				return successful(stereotype, propertyName, tries, checks, randomSeed, generation);
			}

			@Override
			public PropertyExecutionResult changeToFailed(Throwable throwable) {
				return failed(stereotype, propertyName, tries, checks, randomSeed, generation, sample, originalSample, throwable);
			}

		};
	}

	public static PropertyCheckResult exhausted(
		String stereotype,
		String propertyName,
		int tries,
		int checks,
		String randomSeed,
		GenerationMode generation
	) {
		return new PropertyCheckResult(CheckStatus.EXHAUSTED, propertyName, tries, checks, randomSeed, generation, null, null, null) {
			@Override
			public String toString() {
				int rejections = tries - checks;
				return String.format("%s [%s] exhausted after [%d] tries and [%d] rejections", stereotype, propertyName, tries, rejections);
			}

			@Override
			public PropertyExecutionResult changeToSuccessful() {
				return successful(stereotype, propertyName, tries, checks, randomSeed, generation);
			}

			@Override
			public PropertyExecutionResult changeToFailed(Throwable throwable) {
				return failed(stereotype, propertyName, tries, checks, randomSeed, generation, null, null, throwable);
			}

		};
	}

	private final CheckStatus status;
	private final String propertyName;
	private final int tries;
	private final int checks;
	private final String randomSeed;
	private final GenerationMode generation;
	private final List<Object> sample;
	private final List<Object> originalSample;
	private final Throwable throwable;

	private PropertyCheckResult(
		CheckStatus status,
		String propertyName,
		int tries,
		int checks,
		String randomSeed,
		GenerationMode generation,
		List<Object> sample,
		List<Object> originalSample,
		Throwable throwable
	) {
		this.status = status;
		this.propertyName = propertyName;
		this.tries = tries;
		this.checks = checks;
		this.randomSeed = randomSeed;
		this.generation = generation;
		this.sample = sample;
		this.originalSample = originalSample;
		this.throwable = throwable;
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

	public String randomSeed() {
		return randomSeed;
	}

	public Optional<List<Object>> sample() {
		return Optional.ofNullable(sample);
	}

	public Optional<List<Object>> originalSample() {
		return Optional.ofNullable(originalSample);
	}

	public Optional<Throwable> throwable() {
		return Optional.ofNullable(throwable);
	}

	public GenerationMode generation() {
		return generation;
	}

	@Override
	public boolean isExtended() {
		return true;
	}

	@Override
	public Optional<String> getSeed() {
		return Optional.ofNullable(randomSeed());
	}

	@Override
	public Optional<List<Object>> getFalsifiedSample() {
		return sample();
	}

	@Override
	public Status getStatus() {
		return checkStatus() == CheckStatus.SUCCESSFUL ?
				   Status.SUCCESSFUL : Status.FAILED;
	}

	@Override
	public Optional<Throwable> getThrowable() {
		return throwable();
	}

}
