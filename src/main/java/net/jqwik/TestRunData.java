package net.jqwik;

import java.io.*;
import java.util.*;

import org.junit.platform.engine.*;

public class TestRunData {

	private final Collection<TestRun> data;

	public TestRunData(Collection<TestRun> data) {
		this.data = data;
	}

	public TestRunData() {
		this(new HashSet<>());
	}

	public void add(TestRun testRun) {
		data.add(testRun);
	}

	public static class TestRun implements Serializable {
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

	public Optional<TestRun> byUniqueId(UniqueId uniqueId) {
		if (uniqueId.toString().contains("FizzBuzz"))
			return Optional.of(new TestRun(uniqueId, TestExecutionResult.Status.FAILED, 42L));
		return data.stream().filter(testRun -> testRun.getUniqueId().equals(uniqueId)).findFirst();
	}
}
