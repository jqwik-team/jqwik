package net.jqwik.recording;

import org.junit.platform.engine.*;

import java.util.*;

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

	public Optional<TestRun> byUniqueId(UniqueId uniqueId) {
		return data.stream().filter(testRun -> testRun.getUniqueId().equals(uniqueId)).findFirst();
	}
}
