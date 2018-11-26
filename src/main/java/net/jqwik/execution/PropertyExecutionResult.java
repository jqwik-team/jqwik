package net.jqwik.execution;

import java.util.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.TestExecutionResult.*;

public class PropertyExecutionResult {

	private static final PropertyExecutionResult SUCCESSFUL_RESULT = new PropertyExecutionResult(TestExecutionResult.successful());

	private final TestExecutionResult testExecutionResult;

	public PropertyExecutionResult(TestExecutionResult testExecutionResult) {
		this.testExecutionResult = testExecutionResult;
	}

	public static PropertyExecutionResult successful() {
		return SUCCESSFUL_RESULT;
	}

	public static PropertyExecutionResult failed(Throwable throwable) {
		return new PropertyExecutionResult(TestExecutionResult.failed(throwable));
	}

	public static PropertyExecutionResult aborted(Throwable throwable) {
		return new PropertyExecutionResult(TestExecutionResult.aborted(throwable));
	}

	public Status getStatus() {
		return testExecutionResult.getStatus();
	}

	public Optional<Throwable> getThrowable() {
		return testExecutionResult.getThrowable();
	}

	@Override
	public String toString() {
		return String.format("PropertyExecutionResult[%s]", testExecutionResult.getStatus());
	}

	public TestExecutionResult getResult() {
		return testExecutionResult;
	}
}
