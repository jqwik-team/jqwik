package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.support.*;

public class PropertyCheckResult implements ExtendedPropertyExecutionResult {

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
		return new PropertyCheckResult(
			stereotype,
			CheckStatus.SUCCESSFUL,
			propertyName,
			tries,
			checks,
			randomSeed,
			generation,
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
		List<Object> sample,
		List<Object> originalSample,
		Throwable throwable
	) {
		return new PropertyCheckResult(
			stereotype,
			CheckStatus.FAILED,
			propertyName,
			tries,
			checks,
			randomSeed,
			generation,
			sample,
			originalSample,
			throwable
		);
	}

	public static PropertyCheckResult exhausted(
		String stereotype,
		String propertyName,
		int tries,
		int checks,
		String randomSeed,
		GenerationMode generation
	) {
		return new PropertyCheckResult(
			stereotype,
			CheckStatus.EXHAUSTED,
			propertyName,
			tries,
			checks,
			randomSeed,
			generation,
			null,
			null,
			null
		);
	}

	private final String stereotype;
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
		String stereotype,
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
		this.stereotype = stereotype;
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

	@Override
	public Optional<List<Object>> falsifiedSample() {
		return Optional.ofNullable(sample);
	}

	@Override
	public Optional<String> seed() {
		return Optional.ofNullable(randomSeed());
	}

	@Override
	public Status status() {
		return checkStatus() == CheckStatus.SUCCESSFUL ? Status.SUCCESSFUL : Status.FAILED;
	}

	@Override
	public Optional<Throwable> throwable() {
		return Optional.ofNullable(throwable);
	}

	@Override
	public PropertyExecutionResult changeToSuccessful() {
		if (status() == Status.SUCCESSFUL) {
			return this;
		}
		return successful(stereotype, propertyName, tries, checks, randomSeed, generation);
	}

	@Override
	public PropertyExecutionResult changeToFailed(Throwable throwable) {
		return failed(stereotype, propertyName, tries, checks, randomSeed, generation, sample, originalSample, throwable);
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

	public String randomSeed() {
		return randomSeed;
	}

	public Optional<List<Object>> originalSample() {
		return Optional.ofNullable(originalSample);
	}

	public GenerationMode generation() {
		return generation;
	}

	@Override
	public String toString() {
		String header = String.format("%s [%s] failed", stereotype, propertyName);
		switch (checkStatus()) {
			case FAILED:
				String sampleString = sample.isEmpty() ? "" : String.format(" with sample %s", JqwikStringSupport.displayString(sample));
				return String.format("%s%s", header, sampleString);
			case EXHAUSTED:
				int rejections = tries - checks;
				return String.format("%s after [%d] tries and [%d] rejections", header, tries, rejections);

			default:
				return header;
		}
	}

}
