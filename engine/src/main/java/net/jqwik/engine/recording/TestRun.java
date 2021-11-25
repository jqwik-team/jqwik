package net.jqwik.engine.recording;

import java.io.*;
import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.PropertyExecutionResult.*;
import net.jqwik.engine.support.*;

public class TestRun implements Serializable {
	private final String uniqueIdString;
	private final ParametersHash parametersHash;
	private final int statusOrdinal;
	private final String randomSeed;
	private final List<Object> falsifiedSample;

	public TestRun(
		UniqueId uniqueId,
		ParametersHash parametersHash,
		Status status,
		String randomSeed,
		List<Object> falsifiedSample
	) {
		this.uniqueIdString = uniqueId.toString();
		this.parametersHash = parametersHash;
		this.statusOrdinal = status.ordinal();
		this.randomSeed = randomSeed;
		this.falsifiedSample = falsifiedSample;
	}

	boolean hasUniqueId(UniqueId uniqueId) {
		return getUniqueId().equals(uniqueId);
	}

	public boolean isNotSuccessful() {
		return getStatus() != Status.SUCCESSFUL;
	}

	public UniqueId getUniqueId() {
		return UniqueId.parse(uniqueIdString);
	}

	public ParametersHash getParametersHash() {
		return parametersHash;
	}

	public Status getStatus() {
		return Status.values()[statusOrdinal];
	}

	public Optional<String> randomSeed() {
		return Optional.ofNullable(randomSeed);
	}

	public Optional<List<Object>> falsifiedSample() {
		return Optional.ofNullable(falsifiedSample);
	}

	@Override
	public String toString() {
		String randomSeedString = randomSeed().map(s -> ":" + s).orElse("");
		return String.format("TestRun[%s:%s%s]", uniqueIdString, getStatus(), randomSeedString);
	}

	TestRun withoutFalsifiedSample() {
		return new TestRun(getUniqueId(), parametersHash, getStatus(), randomSeed, null);
	}
}
