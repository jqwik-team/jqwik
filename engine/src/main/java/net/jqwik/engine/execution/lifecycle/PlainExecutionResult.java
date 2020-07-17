package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class PlainExecutionResult implements ExtendedPropertyExecutionResult {

	public static ExtendedPropertyExecutionResult successful() {
		return new PlainExecutionResult(Status.SUCCESSFUL, null, null);
	}

	private static ExtendedPropertyExecutionResult successful(String seed) {
		return new PlainExecutionResult(Status.SUCCESSFUL, seed, null);
	}

	public static ExtendedPropertyExecutionResult failed(Throwable throwable, String seed) {
		if (throwable == null) {
			throw new IllegalArgumentException("throwable must never be null for failed PropertyExecutionResult");
		}
		return new PlainExecutionResult(Status.FAILED, seed, throwable);
	}

	public static ExtendedPropertyExecutionResult aborted(Throwable throwable, String seed) {
		if (throwable == null) {
			throw new IllegalArgumentException("throwable must never be null for aborted PropertyExecutionResult");
		}
		return new PlainExecutionResult(Status.ABORTED, seed, throwable);
	}

	private final Status status;
	private final String seed;
	private final Throwable throwable;

	private PlainExecutionResult(Status status, String seed, Throwable throwable) {
		this.status = status;
		this.seed = seed != null ? (seed.isEmpty() ? null : seed) : null;
		this.throwable = throwable;
	}

	@Override
	public Optional<String> seed() {
		return Optional.ofNullable(seed);
	}

	@Override
	public Optional<List<Object>> falsifiedSample() {
		return Optional.empty();
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
	public PropertyExecutionResult mapTo(Status newStatus, Throwable throwable) {
		return new PlainExecutionResult(newStatus, seed, throwable);
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
	public Optional<List<Object>> shrunkSample() {
		return Optional.empty();
	}

	@Override
	public GenerationMode generation() {
		return GenerationMode.NOT_SET;
	}

	@Override
	public EdgeCasesExecutionResult edgeCases() {
		return new EdgeCasesExecutionResult(EdgeCasesMode.NOT_SET, 0, 0);
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
