package net.jqwik.execution;

import java.util.logging.Logger;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.hierarchical.SingleTestExecutor;

import net.jqwik.api.ExampleLifecycle;
import net.jqwik.api.PropertyLifecycle;
import net.jqwik.descriptor.ContainerClassDescriptor;
import net.jqwik.descriptor.ExampleMethodDescriptor;
import net.jqwik.descriptor.JqwikEngineDescriptor;
import net.jqwik.descriptor.PropertyMethodDescriptor;

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
		LOG.warning(() -> String.format("Cannot execute descriptor [%s]", descriptor));
	}

	private void executeProperty(PropertyMethodDescriptor propertyMethodDescriptor, EngineExecutionListener listener) {
		PropertyLifecycle lifecycle = registry.lifecycleFor(propertyMethodDescriptor);
		propertyExecutor.execute(propertyMethodDescriptor, listener, lifecycle);
	}

	private void executeExample(ExampleMethodDescriptor exampleMethodDescriptor, EngineExecutionListener listener) {
		ExampleLifecycle lifecycle = registry.lifecycleFor(exampleMethodDescriptor);
		exampleExecutor.execute(exampleMethodDescriptor, listener, lifecycle);
	}

	private void executeContainer(TestDescriptor containerDescriptor, EngineExecutionListener listener) {
		containerExecutor.execute(containerDescriptor, listener, childExecutor);
	}

}
