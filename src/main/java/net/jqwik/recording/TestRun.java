package net.jqwik.recording;

import org.junit.platform.engine.*;

import java.io.*;

public class TestRun implements Serializable {
	private final UniqueId uniqueId;
	private final TestExecutionResult.Status status;
	private final long randomSeed;

	public TestRun(UniqueId uniqueId, TestExecutionResult.Status status, long randomSeed) {
		this.uniqueId = uniqueId;
		this.status = status;
		this.randomSeed = randomSeed;
	}
	public UniqueId getUniqueId() {
		return uniqueId;
	}

	public TestExecutionResult.Status getStatus() {
		return status;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

}
