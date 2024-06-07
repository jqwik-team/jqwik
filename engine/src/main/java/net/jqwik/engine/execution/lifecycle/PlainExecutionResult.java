package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.*;

import org.jspecify.annotations.*;

public class PlainExecutionResult implements ExtendedPropertyExecutionResult {

	public static ExtendedPropertyExecutionResult successful() {
		return new PlainExecutionResult(Status.SUCCESSFUL, new GenerationInfo(null), null);
	}

	public static ExtendedPropertyExecutionResult failed(Throwable throwable, @Nullable String seed) {
		if (throwable == null) {
			throw new IllegalArgumentException("throwable must never be null for failed PropertyExecutionResult");
		}
		return new PlainExecutionResult(Status.FAILED, new GenerationInfo(seed), throwable);
	}

	public static ExtendedPropertyExecutionResult aborted(Throwable throwable, String seed) {
		return aborted(throwable, new GenerationInfo(seed));
	}

	public static ExtendedPropertyExecutionResult aborted(Throwable throwable, GenerationInfo generationInfo) {
		if (throwable == null) {
			throw new IllegalArgumentException("throwable must never be null for aborted PropertyExecutionResult");
		}
		return new PlainExecutionResult(Status.ABORTED, generationInfo, throwable);
	}

	private final Status status;
	private final GenerationInfo generationInfo;
	private final @Nullable Throwable throwable;

	private PlainExecutionResult(Status status, GenerationInfo generationInfo, @Nullable Throwable throwable) {
		this.status = status;
		this.generationInfo = generationInfo;
		this.throwable = throwable;
	}

	@Override
	public Optional<List<Object>> falsifiedParameters() {
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
	public PropertyExecutionResult mapTo(Status newStatus, @Nullable Throwable throwable) {
		return new PlainExecutionResult(newStatus, generationInfo, throwable);
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
	public Optional<FalsifiedSample> originalSample() {
		return Optional.empty();
	}

	@Override
	public Optional<ShrunkFalsifiedSample> shrunkSample() {
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
	public GenerationInfo generationInfo() {
		return generationInfo;
	}

	@Override
	public String toString() {
		return String.format("PlainExecutionResult[%s]", status);
	}
}
