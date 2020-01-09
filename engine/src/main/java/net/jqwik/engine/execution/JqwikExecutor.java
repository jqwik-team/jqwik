package net.jqwik.engine.execution;

import java.util.*;
import java.util.logging.*;

import org.junit.platform.engine.*;

import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.execution.pipeline.*;
import net.jqwik.engine.recording.*;

public class JqwikExecutor {

	private final LifecycleHooksRegistry registry;
	private final TestRunRecorder recorder;
	private final Set<UniqueId> previousFailedTests;
	private final boolean useJunitPlatformReporter;
	private final boolean reportOnlyFailures;
	private final PropertyTaskCreator propertyTaskCreator = new PropertyTaskCreator();
	private final ContainerTaskCreator containerTaskCreator = new ContainerTaskCreator();
	private final ExecutionTaskCreator childTaskCreator = this::createTask;

	private static final Logger LOG = Logger.getLogger(JqwikExecutor.class.getName());

	public JqwikExecutor(
		LifecycleHooksRegistry registry,
		TestRunRecorder recorder,
		Set<UniqueId> previousFailedTests,
		boolean useJunitPlatformReporter,
		boolean reportOnlyFailures
	) {
		this.registry = registry;
		this.recorder = recorder;
		this.previousFailedTests = previousFailedTests;
		this.useJunitPlatformReporter = useJunitPlatformReporter;
		this.reportOnlyFailures = reportOnlyFailures;
	}

	public void execute(TestDescriptor descriptor, EngineExecutionListener engineExecutionListener) {
		PropertyExecutionListener recordingListener = new RecordingExecutionListener(recorder, engineExecutionListener, useJunitPlatformReporter);
		ExecutionPipeline pipeline = new ExecutionPipeline(recordingListener);
		ExecutionTask mainTask = createTask(descriptor, pipeline);
		pipeline.submit(mainTask);
		letNonSuccessfulTestsExecuteFirst(pipeline);
		pipeline.runToTermination();
	}

	private void letNonSuccessfulTestsExecuteFirst(ExecutionPipeline pipeline) {
		previousFailedTests.forEach(pipeline::executeFirst);
	}

	private ExecutionTask createTask(TestDescriptor descriptor, Pipeline pipeline) {
		if (descriptor.getClass().equals(JqwikEngineDescriptor.class)) {
			return createContainerTask(descriptor, pipeline);
		}
		if (descriptor.getClass().equals(ContainerClassDescriptor.class)) {
			return createContainerTask(descriptor, pipeline);
		}
		if (descriptor.getClass().equals(PropertyMethodDescriptor.class)) {
			return createPropertyTask((PropertyMethodDescriptor) descriptor, pipeline);
		}
		if (descriptor.getClass().equals(SkipExecutionDecorator.class)) {
			return createSkippingTask((SkipExecutionDecorator) descriptor, pipeline);
		}
		return ExecutionTask.from(listener -> LOG.warning(() -> String.format("Cannot execute descriptor [%s]", descriptor)),
								  descriptor.getUniqueId(), "log warning"
		);
	}

	private ExecutionTask createSkippingTask(SkipExecutionDecorator descriptor, Pipeline pipeline) {
		String taskDescription = String.format("Skipping [%s] due to: %s", descriptor.getDisplayName(), descriptor.getSkippingReason());
		return ExecutionTask.from(listener -> listener.executionSkipped(descriptor, descriptor.getSkippingReason()),
								  descriptor.getUniqueId(), taskDescription
		);
	}

	private ExecutionTask createPropertyTask(
		PropertyMethodDescriptor propertyMethodDescriptor,
		Pipeline pipeline
	) {
		return propertyTaskCreator.createTask(propertyMethodDescriptor, registry, reportOnlyFailures);
	}

	private ExecutionTask createContainerTask(TestDescriptor containerDescriptor, Pipeline pipeline) {
		return containerTaskCreator.createTask(containerDescriptor, childTaskCreator, pipeline, registry);
	}

}
