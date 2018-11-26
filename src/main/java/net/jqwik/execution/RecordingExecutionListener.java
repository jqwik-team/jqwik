package net.jqwik.execution;

import java.util.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.properties.*;
import net.jqwik.recording.*;

public class RecordingExecutionListener implements PropertyExecutionListener {

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
	public void executionFinished(TestDescriptor testDescriptor, PropertyExecutionResult executionResult) {
		recordTestRun(testDescriptor, executionResult);
		listener.executionFinished(testDescriptor, executionResult.getResult());
	}

	private void recordTestRun(TestDescriptor testDescriptor, PropertyExecutionResult executionResult) {
		String seed = seeds.computeIfAbsent(testDescriptor, ignore -> Property.SEED_NOT_SET);

		// TODO: Remove when seed recording has been moved
		if (!seed.isEmpty() && !seed.equals(executionResult.getSeed())) {
			throw  new JqwikException(String.format("SEED DIFFERENCE! From report: %s. From result: %s" , seed, executionResult.getSeed()));
		}

		TestRun run = new TestRun(testDescriptor.getUniqueId(), executionResult.getStatus(), seed);
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
