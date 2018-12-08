package net.jqwik.engine.execution;

import java.io.*;
import java.util.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.recording.*;

public class RecordingExecutionListener implements PropertyExecutionListener {

	private final TestRunRecorder recorder;
	private final EngineExecutionListener listener;
	private final boolean useJunitPlatformReporter;

	RecordingExecutionListener(TestRunRecorder recorder, EngineExecutionListener listener, boolean useJunitPlatformReporter) {
		this.recorder = recorder;
		this.listener = listener;
		this.useJunitPlatformReporter = useJunitPlatformReporter;
	}

	@Override
	public void executionSkipped(TestDescriptor testDescriptor, String reason) {
		listener.executionSkipped(testDescriptor, reason);
	}

	@Override
	public void executionStarted(TestDescriptor testDescriptor) {
		listener.executionStarted(testDescriptor);
	}

	@Override
	public void executionFinished(TestDescriptor testDescriptor, PropertyExecutionResult executionResult) {
		recordTestRun(testDescriptor, executionResult);
		listener.executionFinished(testDescriptor, toTestExecutionResult(executionResult));
	}

	private TestExecutionResult toTestExecutionResult(PropertyExecutionResult executionResult) {
		if (executionResult.getStatus() == PropertyExecutionResult.Status.SUCCESSFUL) {
			return TestExecutionResult.successful();
		}
		if (executionResult.getStatus() == PropertyExecutionResult.Status.FAILED) {
			return TestExecutionResult.failed(executionResult.getThrowable().orElse(null));
		}
		if (executionResult.getStatus() == PropertyExecutionResult.Status.ABORTED) {
			return TestExecutionResult.aborted(executionResult.getThrowable().orElse(null));
		}
		throw new IllegalArgumentException("No other status possible");
	}

	private void recordTestRun(TestDescriptor testDescriptor, PropertyExecutionResult executionResult) {
		String seed = executionResult.getSeed().orElse(null);
		List<Object> sample = executionResult.getFalsifiedSample()
											 .filter(this::isSerializable)
											 .orElse(null);
		TestRun run = new TestRun(testDescriptor.getUniqueId(), executionResult.getStatus(), seed, sample);
		recorder.record(run);
	}

	private boolean isSerializable(List<Object> sample) {
		if (!(sample instanceof Serializable)) {
			return false;
		}
		return sample.stream().allMatch(e -> e instanceof Serializable);
	}

	@Override
	public void reportingEntryPublished(TestDescriptor testDescriptor, ReportEntry entry) {
		if (useJunitPlatformReporter) {
			listener.reportingEntryPublished(testDescriptor, entry);
		} else {
			ReportEntrySupport.printToStdout(testDescriptor, entry);
		}
	}

}
