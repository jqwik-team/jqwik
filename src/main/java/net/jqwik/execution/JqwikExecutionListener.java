package net.jqwik.execution;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;

public interface JqwikExecutionListener {

	void executionSkipped(TestDescriptor testDescriptor, String reason);

	void executionStarted(TestDescriptor testDescriptor);

	void executionFinished(TestDescriptor testDescriptor, TestExecutionResult testExecutionResult);

	void reportingEntryPublished(TestDescriptor testDescriptor, ReportEntry entry);

}
