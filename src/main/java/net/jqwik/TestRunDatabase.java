package net.jqwik;

import net.jqwik.recording.*;

import java.nio.file.*;

public class TestRunDatabase implements TestRunRecorder {

	TestRunDatabase(Path databasePath) {

	}

	@Override
	public void record(TestRun testRun) {
	}

	public TestRunData previousRun() {
		return new TestRunData();
	}
}
