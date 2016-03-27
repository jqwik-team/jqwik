
package net.jqwik;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.gen5.engine.EngineExecutionListener;
import org.junit.gen5.engine.TestDescriptor;
import org.junit.gen5.engine.TestExecutionResult;
import org.junit.gen5.engine.reporting.ReportEntry;

public class RecordingExecutionListener implements EngineExecutionListener {
	private final List<ExecutionEvent> events = new ArrayList<>();

	@Override
	public void dynamicTestRegistered(TestDescriptor testDescriptor) {

	}

	@Override
	public void executionSkipped(TestDescriptor testDescriptor, String reason) {
		events.add(new ExecutionEvent(testDescriptor, ExecutionEventType.Skipped));
	}

	@Override
	public void executionStarted(TestDescriptor testDescriptor) {
		events.add(new ExecutionEvent(testDescriptor, ExecutionEventType.Started));
	}

	@Override
	public void executionFinished(TestDescriptor testDescriptor, TestExecutionResult testExecutionResult) {
		switch (testExecutionResult.getStatus()) {
			case SUCCESSFUL:
				events.add(new ExecutionEvent(testDescriptor, ExecutionEventType.Successful));
				break;
			case FAILED:
				events.add(new ExecutionEvent(testDescriptor, ExecutionEventType.Failed));
				break;
			default:
				;
		}
	}

	@Override
	public void reportingEntryPublished(TestDescriptor testDescriptor, ReportEntry entry) {

	}

	public long countPropertiesStarted() {
		return countPropertiesEvent(ExecutionEventType.Started);
	}

	public long countPropertiesSuccessful() {
		return countPropertiesEvent(ExecutionEventType.Successful);
	}

	public long countPropertiesFailed() {
		return countPropertiesEvent(ExecutionEventType.Failed);
	}

	public long countPropertiesSkipped() {
		ExecutionEventType eventType = ExecutionEventType.Skipped;
		return countPropertiesEvent(eventType);
	}

	private long countPropertiesEvent(ExecutionEventType eventType) {
		return propertiesEventStream().filter(event -> event.type == eventType).count();
	}

	private Stream<ExecutionEvent> propertiesEventStream() {
		return events.stream().filter(event -> event.descriptor instanceof JqwikPropertyDescriptor);
	}

	public class ExecutionEvent {
		final TestDescriptor descriptor;
		final ExecutionEventType type;

		public ExecutionEvent(TestDescriptor descriptor, ExecutionEventType type) {
			this.descriptor = descriptor;
			this.type = type;
		}
	}

	public enum ExecutionEventType {
		Skipped, Started, Successful, Failed
	}
}
