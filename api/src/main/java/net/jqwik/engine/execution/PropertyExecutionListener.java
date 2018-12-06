package net.jqwik.engine.execution;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;

public interface PropertyExecutionListener {

	void executionSkipped(TestDescriptor testDescriptor, String reason);

	void executionStarted(TestDescriptor testDescriptor);

	void executionFinished(TestDescriptor testDescriptor, PropertyExecutionResult executionResult);

	void reportingEntryPublished(TestDescriptor testDescriptor, ReportEntry entry);

}
