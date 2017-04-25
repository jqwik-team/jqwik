package net.jqwik.execution;

import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.pipeline.*;
import net.jqwik.recording.*;

public class JqwikExecutor {

	private final LifecycleRegistry registry;
	private final TestRunRecorder recorder;
	private final PropertyTaskCreator propertyTaskCreator = new PropertyTaskCreator();
	private final ContainerTaskCreator containerTaskCreator = new ContainerTaskCreator();
	private final ExecutionTaskCreator childTaskCreator = this::createTask;

	private static final Logger LOG = Logger.getLogger(JqwikExecutor.class.getName());

	public JqwikExecutor(LifecycleRegistry registry, TestRunRecorder recorder) {
		this.registry = registry;
		this.recorder = recorder;
	}

	public void execute(ExecutionRequest request, TestDescriptor descriptor) {
		ExecutionPipeline pipeline = new ExecutionPipeline(request.getEngineExecutionListener());
		ExecutionTask mainTask = createTask(descriptor, pipeline);
		pipeline.submit(mainTask);
		pipeline.runToTermination();
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
