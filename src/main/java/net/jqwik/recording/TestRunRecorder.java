package net.jqwik.recording;

public interface TestRunRecorder {
	void record(TestRun testRun);

	TestRunRecorder NULL = new TestRunRecorder() {
		@Override
		public void record(TestRun testRun) {

		}
	};
}
