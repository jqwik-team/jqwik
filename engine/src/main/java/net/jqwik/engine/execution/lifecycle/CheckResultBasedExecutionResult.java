package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.opentest4j.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

public class CheckResultBasedExecutionResult implements ExtendedPropertyExecutionResult {

	public static ExtendedPropertyExecutionResult from(PropertyCheckResult checkResult) {
		Status status;
		Throwable throwable = null;
		if (checkResult.status() == PropertyCheckResult.Status.SATISFIED) {
			status = Status.SUCCESSFUL;
		} else {
			status = Status.FAILED;
			throwable = checkResult.throwable().orElse(new AssertionFailedError(checkResult.toString()));
		}
		return new CheckResultBasedExecutionResult(checkResult, status, throwable);
	}


	private final PropertyCheckResult checkResult;
	private final Status status;
	private Throwable throwable;

	private CheckResultBasedExecutionResult(PropertyCheckResult checkResult, Status status, Throwable throwable) {
		this.checkResult = checkResult;
		this.status = status;
		this.throwable = throwable;
	}

	@Override
	public Optional<String> getSeed() {
		return Optional.ofNullable(checkResult.randomSeed());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<List<Object>> getFalsifiedSample() {
		return checkResult.sample().map(list -> (List<Object>) list);
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public Optional<Throwable> getThrowable() {
		return Optional.ofNullable(throwable);
	}

	@Override
	public PropertyExecutionResult changeToSuccessful() {
		return new CheckResultBasedExecutionResult(checkResult, Status.SUCCESSFUL, null);
	}

	@Override
	public PropertyExecutionResult changeToFailed(Throwable throwable) {
		return new CheckResultBasedExecutionResult(checkResult, Status.FAILED, throwable);
	}

	@Override
	public String toString() {
		return String.format("CheckResultBasedExecutionResult[%s]", status);
	}

	@Override
	public Optional<PropertyCheckResult> checkResult() {
		return Optional.ofNullable(checkResult);
	}
}
