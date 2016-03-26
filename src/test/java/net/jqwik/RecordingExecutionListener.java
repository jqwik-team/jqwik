package net.jqwik;

import org.junit.gen5.engine.EngineExecutionListener;
import org.junit.gen5.engine.TestDescriptor;
import org.junit.gen5.engine.TestExecutionResult;
import org.junit.gen5.engine.reporting.ReportEntry;

public class RecordingExecutionListener implements EngineExecutionListener {
	public long propertiesStarted = 0;
	public long propertiesSuccessful = 0;
	public long propertiesFailed = 0;
	public long propertiesSkipped = 0;

	@Override
	public void dynamicTestRegistered(TestDescriptor testDescriptor) {

	}

	@Override
	public void executionSkipped(TestDescriptor testDescriptor, String reason) {
		if (!(testDescriptor instanceof JqwikPropertyDescriptor))
			return;
		propertiesSkipped++;
	}

	@Override
	public void executionStarted(TestDescriptor testDescriptor) {
		if (!(testDescriptor instanceof JqwikPropertyDescriptor))
			return;
		propertiesStarted++;
	}

	@Override
	public void executionFinished(TestDescriptor testDescriptor, TestExecutionResult testExecutionResult) {
		if (!(testDescriptor instanceof JqwikPropertyDescriptor))
			return;
		switch (testExecutionResult.getStatus()) {
			case SUCCESSFUL:
				propertiesSuccessful++;
				break;
			case FAILED:
				propertiesFailed++;
				break;
			default:;
		}
	}

	@Override
	public void reportingEntryPublished(TestDescriptor testDescriptor, ReportEntry entry) {

	}
}
