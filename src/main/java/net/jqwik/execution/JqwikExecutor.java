package net.jqwik.execution;

import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.pipeline.*;
import net.jqwik.execution.pipeline.Pipeline.*;

public class JqwikExecutor {

	private final LifecycleRegistry registry;
	private final PropertyExecutor propertyExecutor = new PropertyExecutor();
	private final ContainerExecutor containerExecutor = new ContainerExecutor();
	private final TestDescriptorExecutor childExecutor = this::execute;

	private static final Logger LOG = Logger.getLogger(JqwikExecutor.class.getName());

	public JqwikExecutor(LifecycleRegistry registry) {
		this.registry = registry;
	}

	public void execute(ExecutionRequest request, TestDescriptor descriptor) {
		ExecutionPipeline pipeline = new ExecutionPipeline(request.getEngineExecutionListener());
		execute(descriptor, pipeline);
		pipeline.waitForTermination();
	}

	private void execute(TestDescriptor descriptor, Pipeline pipeline, ExecutionTask... predecessors) {
		if (descriptor.getClass().equals(JqwikEngineDescriptor.class)) {
			executeContainer(descriptor, pipeline, predecessors);
			return;
		}
		if (descriptor.getClass().equals(ContainerClassDescriptor.class)) {
			executeContainer(descriptor, pipeline, predecessors);
			return;
		}
		if (descriptor.getClass().equals(PropertyMethodDescriptor.class)) {
			executeProperty((PropertyMethodDescriptor) descriptor, pipeline, predecessors);
			return;
		}
		if (descriptor.getClass().equals(SkipExecutionDecorator.class)) {
			executeSkipping((SkipExecutionDecorator) descriptor, pipeline, predecessors);
			return;
		}
		LOG.warning(() -> String.format("Cannot execute descriptor [%s]", descriptor));
	}

	private void executeSkipping(SkipExecutionDecorator descriptor, Pipeline pipeline, ExecutionTask[] predecessors) {
		pipeline.submit(listener -> listener.executionSkipped(descriptor, descriptor.getSkippingReason()), predecessors);
	}

	private void executeProperty(PropertyMethodDescriptor propertyMethodDescriptor, Pipeline pipeline, ExecutionTask[] predecessors) {
//		Function<Object, PropertyLifecycle> lifecycleSupplier = registry.supplierFor(propertyMethodDescriptor);
//		propertyExecutor.execute(propertyMethodDescriptor, pipeline, lifecycleSupplier);
	}

	private void executeContainer(TestDescriptor containerDescriptor, Pipeline pipeline, ExecutionTask[] predecessors) {
		containerExecutor.execute(containerDescriptor, childExecutor, pipeline, predecessors);
	}

}
