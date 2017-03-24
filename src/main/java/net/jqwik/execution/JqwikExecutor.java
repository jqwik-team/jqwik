package net.jqwik.execution;

import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.properties.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.properties.*;

public class JqwikExecutor {

	private final LifecycleRegistry registry;
	private final ExampleExecutor exampleExecutor = new ExampleExecutor();
	private final PropertyExecutor propertyExecutor = new PropertyExecutor();
	private final ContainerExecutor containerExecutor = new ContainerExecutor();
	private final TestDescriptorExecutor childExecutor = this::execute;

	private static final Logger LOG = Logger.getLogger(JqwikExecutor.class.getName());

	public JqwikExecutor(LifecycleRegistry registry) {
		this.registry = registry;
	}

	public void execute(ExecutionRequest request, TestDescriptor descriptor) {
		execute(descriptor, request.getEngineExecutionListener());
	}

	private void execute(TestDescriptor descriptor, EngineExecutionListener listener) {
		if (descriptor.getClass().equals(JqwikEngineDescriptor.class)) {
			executeContainer(descriptor, listener);
			return;
		}
		if (descriptor.getClass().equals(ContainerClassDescriptor.class)) {
			executeContainer(descriptor, listener);
			return;
		}
		if (descriptor.getClass().equals(ExampleMethodDescriptor.class)) {
			executeExample((ExampleMethodDescriptor) descriptor, listener);
			return;
		}
		if (descriptor.getClass().equals(PropertyMethodDescriptor.class)) {
			executeProperty((PropertyMethodDescriptor) descriptor, listener);
			return;
		}
		if (descriptor.getClass().equals(SkipExecutionDecorator.class)) {
			executeSkipping((SkipExecutionDecorator) descriptor, listener);
			return;
		}
		LOG.warning(() -> String.format("Cannot execute descriptor [%s]", descriptor));
	}

	private void executeSkipping(SkipExecutionDecorator descriptor, EngineExecutionListener listener) {
		listener.executionSkipped(descriptor, descriptor.getSkippingReason());
	}

	private void executeProperty(PropertyMethodDescriptor propertyMethodDescriptor, EngineExecutionListener listener) {
		Function<Object, PropertyLifecycle> lifecycleSupplier = registry.supplierFor(propertyMethodDescriptor);
		propertyExecutor.execute(propertyMethodDescriptor, listener, lifecycleSupplier);
	}

	private void executeExample(ExampleMethodDescriptor exampleMethodDescriptor, EngineExecutionListener listener) {
		Function<Object, ExampleLifecycle> lifecycleSupplier = registry.supplierFor(exampleMethodDescriptor);
		exampleExecutor.execute(exampleMethodDescriptor, listener, lifecycleSupplier);
	}

	private void executeContainer(TestDescriptor containerDescriptor, EngineExecutionListener listener) {
		containerExecutor.execute(containerDescriptor, listener, childExecutor);
	}

}
