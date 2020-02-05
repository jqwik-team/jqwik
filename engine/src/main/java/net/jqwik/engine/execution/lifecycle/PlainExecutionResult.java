package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

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
	public Optional<String> seed() {
		return Optional.ofNullable(seed);
	}

	@Override
	public Optional<List<Object>> falsifiedSample() {
		return Optional.ofNullable(falsifiedSample);
	}

	@Override
	public Status status() {
		return status;
	}

	@Override
	public Optional<Throwable> throwable() {
		return Optional.ofNullable(throwable);
	}

	@Override
	public PropertyExecutionResult changeToSuccessful() {
		return PlainExecutionResult.successful(seed().orElse(null));
	}

	@Override
	public PropertyExecutionResult changeToFailed(Throwable throwable) {
		return PlainExecutionResult.failed(throwable, seed().orElse(null), falsifiedSample().orElse(null));
	}

	@Override
	public boolean isExtended() {
		return false;
	}

	@Override
	public int countChecks() {
		return 0;
	}

	@Override
	public int countTries() {
		return 0;
	}

	@Override
	public Optional<List<Object>> originalSample() {
		return Optional.empty();
	}

	@Override
	public GenerationMode generation() {
		return GenerationMode.NOT_SET;
	}

	@Override
	public String randomSeed() {
		return Long.toString(0L);
	}

	@Override
	public String toString() {
		return String.format("PlainPropertyExecutionResult[%s]", status);
	}
}
