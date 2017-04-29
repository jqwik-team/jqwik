package net.jqwik.recording;

import java.io.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.TestExecutionResult.*;

import net.jqwik.discovery.*;

public class TestRun implements Serializable {
	public static final TestRun NULL = new TestRun(UniqueId.root("no", "value"), Status.FAILED, 0L);

	private final String uniqueIdString;
	private final int statusOrdinal;
	private final long randomSeed;

	public TestRun(UniqueId uniqueId, Status status, long randomSeed) {
		this.uniqueIdString = JqwikUniqueIDs.toString(uniqueId);
		this.statusOrdinal = status.ordinal();
		this.randomSeed = randomSeed;
	}
	public UniqueId getUniqueId() {
		return JqwikUniqueIDs.parse(uniqueIdString);
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
