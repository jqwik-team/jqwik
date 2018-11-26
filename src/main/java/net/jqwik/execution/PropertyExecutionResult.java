package net.jqwik.execution;

import java.util.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.TestExecutionResult.*;

public class PropertyExecutionResult {

	private final TestExecutionResult testExecutionResult;
	private final String seed;

	public PropertyExecutionResult(TestExecutionResult testExecutionResult, String seed) {
		this.testExecutionResult = testExecutionResult;
		this.seed = seed;
	}

	public static PropertyExecutionResult successful() {
		return new PropertyExecutionResult(TestExecutionResult.successful(), null);
	}

	public static PropertyExecutionResult successful(String seed) {
		return new PropertyExecutionResult(TestExecutionResult.successful(), seed);
	}

	public static PropertyExecutionResult failed(Throwable throwable, String seed) {
		return new PropertyExecutionResult(TestExecutionResult.failed(throwable), seed);
	}

	public static PropertyExecutionResult aborted(Throwable throwable, String seed) {
		return new PropertyExecutionResult(TestExecutionResult.aborted(throwable), seed);
	}

	public Optional<String> getSeed() {
		return Optional.ofNullable(seed);
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
