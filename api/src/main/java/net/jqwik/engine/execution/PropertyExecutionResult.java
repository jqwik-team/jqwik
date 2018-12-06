package net.jqwik.engine.execution;

import java.util.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.TestExecutionResult.*;

public class PropertyExecutionResult {

	private final TestExecutionResult testExecutionResult;
	private final String seed;
	private final List<Object> falsifiedSample;

	public PropertyExecutionResult(TestExecutionResult testExecutionResult, String seed, List<Object> falsifiedSample) {
		this.testExecutionResult = testExecutionResult;
		this.seed = seed != null ? (seed.isEmpty() ? null : seed) : null;
		this.falsifiedSample = falsifiedSample;
	}

	public static PropertyExecutionResult successful() {
		return new PropertyExecutionResult(TestExecutionResult.successful(), null, null);
	}

	public static PropertyExecutionResult successful(String seed) {
		return new PropertyExecutionResult(TestExecutionResult.successful(), seed, null);
	}

	public static PropertyExecutionResult failed(Throwable throwable, String seed, List<Object> sample) {
		return new PropertyExecutionResult(TestExecutionResult.failed(throwable), seed, sample);
	}

	public static PropertyExecutionResult aborted(Throwable throwable, String seed) {
		return new PropertyExecutionResult(TestExecutionResult.aborted(throwable), seed, null);
	}

	public Optional<String> getSeed() {
		return Optional.ofNullable(seed);
	}

	public Optional<List<Object>> getFalsifiedSample() {
		return Optional.ofNullable(falsifiedSample);
	}

	public Status getStatus() {
		return testExecutionResult.getStatus();
	}

	public Optional<Throwable> getThrowable() {
		return testExecutionResult.getThrowable();
	}

	public TestExecutionResult getResult() {
		return testExecutionResult;
	}

	@Override
	public String toString() {
		return String.format("PropertyExecutionResult[%s]", testExecutionResult.getStatus());
	}

}
