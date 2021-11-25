package net.jqwik.engine.execution;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.recording.*;
import net.jqwik.engine.support.*;

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
		if (executionResult.status() == PropertyExecutionResult.Status.SUCCESSFUL) {
			return TestExecutionResult.successful();
		}
		if (executionResult.status() == PropertyExecutionResult.Status.FAILED) {
			return TestExecutionResult.failed(executionResult.throwable().orElse(null));
		}
		if (executionResult.status() == PropertyExecutionResult.Status.ABORTED) {
			return TestExecutionResult.aborted(executionResult.throwable().orElse(null));
		}
		throw new IllegalArgumentException("No other status possible");
	}

	private void recordTestRun(TestDescriptor testDescriptor, PropertyExecutionResult executionResult) {
		String seed = executionResult.seed().orElse(null);
		List<Object> sample = executionResult.falsifiedParameters()
											 .filter(this::isSerializable)
											 .orElse(null);
		ParametersHash parametersHash = parametersHash(testDescriptor);
		TestRun run = new TestRun(testDescriptor.getUniqueId(), parametersHash, executionResult.status(), seed, sample);
		recorder.record(run);
	}

	private ParametersHash parametersHash(TestDescriptor testDescriptor) {
		if (testDescriptor instanceof PropertyMethodDescriptor) {
			Method propertyMethod = ((PropertyMethodDescriptor) testDescriptor).getTargetMethod();
			return new ParametersHash(propertyMethod);
		}
		return new ParametersHash(0);
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
