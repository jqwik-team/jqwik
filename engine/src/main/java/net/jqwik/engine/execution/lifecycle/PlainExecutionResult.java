package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

public class PlainExecutionResult implements ExtendedPropertyExecutionResult {

	public static ExtendedPropertyExecutionResult successful() {
		return new PlainExecutionResult(Status.SUCCESSFUL, null, null, null);
	}

	public static ExtendedPropertyExecutionResult successful(String seed) {
		return new PlainExecutionResult(Status.SUCCESSFUL, seed, null, null);
	}

	public static ExtendedPropertyExecutionResult failed(Throwable throwable, String seed, List<Object> sample) {
		return new PlainExecutionResult(Status.FAILED, seed, throwable, sample);
	}

	public static ExtendedPropertyExecutionResult aborted(Throwable throwable, String seed) {
		return new PlainExecutionResult(Status.ABORTED, seed, throwable, null);
	}

	private final Status status;
	private final String seed;
	private final List<Object> falsifiedSample;
	private final Throwable throwable;

	private PlainExecutionResult(Status status, String seed, Throwable throwable, List<Object> falsifiedSample) {
		this.status = status;
		this.seed = seed != null ? (seed.isEmpty() ? null : seed) : null;
		this.throwable = throwable;
		this.falsifiedSample = falsifiedSample;
	}

	@Override
	public Optional<String> getSeed() {
		return Optional.ofNullable(seed);
	}

	@Override
	public Optional<List<Object>> getFalsifiedSample() {
		return Optional.ofNullable(falsifiedSample);
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
		return PlainExecutionResult.successful(getSeed().orElse(null));
	}

	@Override
	public PropertyExecutionResult changeToFailed(Throwable throwable) {
		return PlainExecutionResult.failed(throwable, getSeed().orElse(null), getFalsifiedSample().orElse(null));
	}

	@Override
	public String toString() {
		return String.format("PlainPropertyExecutionResult[%s]", status);
	}

	@Override
	public Optional<PropertyCheckResult> checkResult() {
		return Optional.empty();
	}
}
