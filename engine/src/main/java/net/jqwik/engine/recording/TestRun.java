package net.jqwik.engine.recording;

import java.io.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.PropertyExecutionResult.*;
import net.jqwik.engine.execution.*;

public class TestRun implements Serializable {
	private final String uniqueIdString;
	private final int statusOrdinal;
	private final GenerationInfo generationInfo;

	public TestRun(
		UniqueId uniqueId,
		Status status,
		GenerationInfo generationInfo
	) {
		this.uniqueIdString = uniqueId.toString();
		this.statusOrdinal = status.ordinal();
		this.generationInfo = generationInfo;
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

	public GenerationInfo generationInfo() {
		return generationInfo;
	}

	@Override
	public String toString() {
		return String.format("TestRun[%s:%s:%s]", uniqueIdString, getStatus(), generationInfo);
	}

}
