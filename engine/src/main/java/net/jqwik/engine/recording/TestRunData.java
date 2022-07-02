package net.jqwik.engine.recording;

import java.util.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.engine.support.*;

public class TestRunData {

	private final Collection<TestRun> data;

	public TestRunData(Collection<TestRun> data) {
		this.data = data;
	}

	public TestRunData() {
		this(new LinkedHashSet<>());
	}

	public void add(TestRun testRun) {
		data.add(testRun);
	}

	public Optional<TestRun> byUniqueId(UniqueId uniqueId) {
		try {
			return data.stream()
					   .filter(testRun -> testRun.hasUniqueId(uniqueId))
					   .findFirst();
		} catch (Throwable t) {
			// An exception during test run data read should not stop the test run.
			// Most of the time it's an error due to format change which will go away
			// after one test run where the test run data has been written anew.
			JqwikExceptionSupport.rethrowIfBlacklisted(t);
			return Optional.empty();
		}
	}

	public Stream<TestRun> allNonSuccessfulTests() {
		return data.stream().filter(TestRun::isNotSuccessful);
	}
}
