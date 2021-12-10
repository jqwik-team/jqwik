package net.jqwik.engine.recording;

import java.io.*;
import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.PropertyExecutionResult.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.support.*;

public class TestRun implements Serializable {
	private final String uniqueIdString;
	private final ParametersHash parametersHash;
	private final int statusOrdinal;
	private final GenerationInfo generationInfo;
	private final List<Object> falsifiedSample;

	public TestRun(
		UniqueId uniqueId,
		ParametersHash parametersHash,
		Status status,
		GenerationInfo generationInfo,
		List<Object> falsifiedSample
	) {
		this.uniqueIdString = uniqueId.toString();
		this.parametersHash = parametersHash;
		this.statusOrdinal = status.ordinal();
		this.generationInfo = generationInfo;
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

	public GenerationInfo generationInfo() {
		return generationInfo;
	}

	public Optional<List<Object>> falsifiedSample() {
		return Optional.ofNullable(falsifiedSample);
	}

	@Override
	public String toString() {
		return String.format("TestRun[%s:%s:%s]", uniqueIdString, getStatus(), generationInfo);
	}

	TestRun withoutFalsifiedSample() {
		return new TestRun(getUniqueId(), parametersHash, getStatus(), generationInfo, null);
	}
}
