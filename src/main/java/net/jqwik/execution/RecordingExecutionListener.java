package net.jqwik.execution;

import java.util.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;
import net.jqwik.recording.*;

public class RecordingExecutionListener implements JqwikExecutionListener {

	private final TestRunRecorder recorder;
	private final EngineExecutionListener listener;
	private final boolean useJunitPlatformReporter;
	private Map<TestDescriptor, String> seeds = new IdentityHashMap<>();

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
	public void executionFinished(TestDescriptor testDescriptor, TestExecutionResult testExecutionResult) {
		recordTestRun(testDescriptor, testExecutionResult);
		listener.executionFinished(testDescriptor, testExecutionResult);
	}

	private void recordTestRun(TestDescriptor testDescriptor, TestExecutionResult testExecutionResult) {
		String seed = seeds.computeIfAbsent(testDescriptor, ignore -> Property.SEED_NOT_SET);
		TestRun run = new TestRun(testDescriptor.getUniqueId(), testExecutionResult.getStatus(), seed);
		recorder.record(run);
	}

	@Override
	public void reportingEntryPublished(TestDescriptor testDescriptor, ReportEntry entry) {
		rememberSeed(testDescriptor, entry);

		if (useJunitPlatformReporter) {
			listener.reportingEntryPublished(testDescriptor, entry);
		} else {
			ReportEntrySupport.printToStdout(testDescriptor, entry);
		}
	}

	private void rememberSeed(TestDescriptor testDescriptor, ReportEntry entry) {
		Map<String, String> entries = entry.getKeyValuePairs();
		if (entries.containsKey(CheckResultReportEntry.SEED_REPORT_KEY)) {
			String reportedSeed = getReportedSeed(entries);
			seeds.put(testDescriptor, reportedSeed);
		}
	}

	private String getReportedSeed(Map<String, String> entries) {
		return entries.get(CheckResultReportEntry.SEED_REPORT_KEY);
	}
}
