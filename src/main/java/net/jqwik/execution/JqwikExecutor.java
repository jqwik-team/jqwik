package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.pipeline.*;
import net.jqwik.recording.*;
import org.junit.platform.engine.*;

import java.util.function.*;
import java.util.logging.*;

public class JqwikExecutor {

	private final LifecycleRegistry registry;
	private final TestRunRecorder recorder;
	private final TestRunData previousRun;
	private final PropertyTaskCreator propertyTaskCreator = new PropertyTaskCreator();
	private final ContainerTaskCreator containerTaskCreator = new ContainerTaskCreator();
	private final ExecutionTaskCreator childTaskCreator = this::createTask;

	private static final Logger LOG = Logger.getLogger(JqwikExecutor.class.getName());

	public JqwikExecutor(LifecycleRegistry registry, TestRunRecorder recorder, TestRunData previousRun) {
		this.registry = registry;
		this.recorder = recorder;
		this.previousRun = previousRun;
	}

	public void execute(ExecutionRequest request, TestDescriptor descriptor) {
		EngineExecutionListener recordingListener = new RecordingExecutionListener(recorder, request.getEngineExecutionListener());
		ExecutionPipeline pipeline = new ExecutionPipeline(recordingListener);
		ExecutionTask mainTask = createTask(descriptor, pipeline);
		pipeline.submit(mainTask);
		letNonSuccessfulTestsExecuteFirst(pipeline);
		pipeline.runToTermination();
	}

	public void letNonSuccessfulTestsExecuteFirst(ExecutionPipeline pipeline) {
		previousRun.allNonSuccessfulTests().forEach(testRun -> pipeline.executeFirst(testRun.getUniqueId()));
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
				descriptor.getUniqueId(), "log warning");
	}

	private ExecutionTask createSkippingTask(SkipExecutionDecorator descriptor, Pipeline pipeline) {
		String taskDescription = String.format("Skipping [%s] due to: %s", descriptor.getDisplayName(), descriptor.getSkippingReason());
		return ExecutionTask.from(listener -> listener.executionSkipped(descriptor, descriptor.getSkippingReason()),
				descriptor.getUniqueId(), taskDescription);
	}

	private ExecutionTask createPropertyTask(PropertyMethodDescriptor propertyMethodDescriptor, Pipeline pipeline) {
		Function<Object, PropertyLifecycle> lifecycleSupplier = registry.supplierFor(propertyMethodDescriptor);
		return propertyTaskCreator.createTask(propertyMethodDescriptor, lifecycleSupplier);
	}

	private ExecutionTask createContainerTask(TestDescriptor containerDescriptor, Pipeline pipeline) {
		return containerTaskCreator.createTask(containerDescriptor, childTaskCreator, pipeline);
	}

}
