package net.jqwik.execution.properties;

import org.junit.platform.engine.*;
import org.opentest4j.*;

public class PropertyExecutionResult {

	public static PropertyExecutionResult successful(long seed) {
		return new PropertyExecutionResult(TestExecutionResult.successful(), seed);
	}

	public static PropertyExecutionResult aborted(Throwable throwable, long seed) {
		return new PropertyExecutionResult(TestExecutionResult.aborted(throwable), seed);
	}

	public static PropertyExecutionResult failed(String message, long seed) {
		return new PropertyExecutionResult(TestExecutionResult.failed(new AssertionFailedError(message)), seed);
	}

	private final TestExecutionResult testExecutionResult;
	private final long seed;

	private PropertyExecutionResult(TestExecutionResult testExecutionResult, long seed) {

		this.testExecutionResult = testExecutionResult;
		this.seed = seed;
	}

	public TestExecutionResult getTestExecutionResult() {
		return testExecutionResult;
	}

	public long getSeed() {
		return seed;
	}
}
