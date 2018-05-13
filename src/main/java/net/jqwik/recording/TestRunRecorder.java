package net.jqwik.recording;

public interface TestRunRecorder extends AutoCloseable {
	void record(TestRun testRun);

	default void close() {}

	TestRunRecorder NULL = testRun -> { };
}
