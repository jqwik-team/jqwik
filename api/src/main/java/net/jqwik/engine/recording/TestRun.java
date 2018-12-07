package net.jqwik.engine.recording;

import java.io.*;
import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.PropertyExecutionResult.*;

public class TestRun implements Serializable {
	private final String uniqueIdString;
	private final int statusOrdinal;
	private final String randomSeed;
	private final List falsifiedSample;

	public TestRun(UniqueId uniqueId, PropertyExecutionResult.Status status, String randomSeed, List falsifiedSample) {
		this.uniqueIdString = uniqueId.toString();
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

	public Status getStatus() {
		return Status.values()[statusOrdinal];
	}

	public Optional<String> randomSeed() {
		return Optional.ofNullable(randomSeed);
	}

	public Optional<List> falsifiedSample() {
		return Optional.ofNullable(falsifiedSample);
	}

	@Override
	public String toString() {
		String randomSeedString = randomSeed().map(s -> ":" + s).orElse("");
		return String.format("TestRun[%s:%s%s]", uniqueIdString, getStatus(), randomSeedString);
	}
}
