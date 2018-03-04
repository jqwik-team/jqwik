package net.jqwik.recording;

import org.junit.platform.engine.TestExecutionResult.*;
import org.junit.platform.engine.*;

import java.io.*;

public class TestRun implements Serializable {
	private final String uniqueIdString;
	private final int statusOrdinal;
	private final String randomSeed;

	public TestRun(UniqueId uniqueId, Status status, String randomSeed) {
		this.uniqueIdString = uniqueId.toString();
		this.statusOrdinal = status.ordinal();
		this.randomSeed = randomSeed;
	}
	public UniqueId getUniqueId() {
		return UniqueId.parse(uniqueIdString);
	}

	public Status getStatus() {
		return Status.values()[statusOrdinal];
	}

	public String getRandomSeed() {
		return randomSeed;
	}

	@Override
	public String toString() {
		return String.format("TestRun[%s:%s:%d]", uniqueIdString, getStatus(), randomSeed);
	}
}
