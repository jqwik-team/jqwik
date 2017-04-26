package net.jqwik.recording;

import org.junit.platform.engine.*;
import org.junit.platform.engine.TestExecutionResult.*;

import java.io.*;

public class TestRun implements Serializable {
	private final String uniqueIdString;
	private final int statusOrdinal;
	private final long randomSeed;

	public TestRun(UniqueId uniqueId, Status status, long randomSeed) {
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

	public long getRandomSeed() {
		return randomSeed;
	}

	@Override
	public String toString() {
		return String.format("TestRun[%s:%s:%d]", uniqueIdString, getStatus(), randomSeed);
	}
}
