package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.apiguardian.api.*;
import org.opentest4j.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL, since = "1.2.3")
public class PropertyExecutionResultImpl implements PropertyExecutionResult {

	public static PropertyExecutionResult from(PropertyCheckResult checkResult) {
		if (checkResult.status() == PropertyCheckResult.Status.SATISFIED)
			return PropertyExecutionResultImpl.successful(checkResult.randomSeed());
		Throwable throwable = checkResult.throwable().orElse(new AssertionFailedError(checkResult.toString()));
		List sample = checkResult.sample().orElse(null);
		return PropertyExecutionResultImpl.failed(throwable, checkResult.randomSeed(), sample);
	}

	public static PropertyExecutionResultImpl successful() {
		return new PropertyExecutionResultImpl(Status.SUCCESSFUL, null, null, null);
	}

	public static PropertyExecutionResultImpl successful(String seed) {
		return new PropertyExecutionResultImpl(Status.SUCCESSFUL, seed, null, null);
	}

	public static PropertyExecutionResultImpl failed(Throwable throwable, String seed, List<Object> sample) {
		return new PropertyExecutionResultImpl(Status.FAILED, seed, throwable, sample);
	}

	public static PropertyExecutionResultImpl aborted(Throwable throwable, String seed) {
		return new PropertyExecutionResultImpl(Status.ABORTED, seed, throwable, null);
	}

	private final Status status;
	private final String seed;
	private final List<Object> falsifiedSample;
	private final Throwable throwable;

	private PropertyExecutionResultImpl(Status status, String seed, Throwable throwable, List<Object> falsifiedSample) {
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
	public PropertyExecutionResultImpl changeToSuccessful() {
		return PropertyExecutionResultImpl.successful(getSeed().orElse(null));
	}

	@Override
	public PropertyExecutionResultImpl changeToFailed(Throwable throwable) {
		return PropertyExecutionResultImpl.failed(throwable, getSeed().orElse(null), getFalsifiedSample().orElse(null));
	}

	@Override
	public String toString() {
		return String.format("PropertyExecutionResult[%s]", status);
	}

}
