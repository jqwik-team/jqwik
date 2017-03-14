package net.jqwik.execution;

import net.jqwik.api.ExampleLifecycle;
import net.jqwik.descriptor.JqwikEngineDescriptor;
import net.jqwik.descriptor.OverloadedExampleMethodDescriptor;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import net.jqwik.descriptor.ContainerClassDescriptor;
import net.jqwik.descriptor.ExampleMethodDescriptor;
import org.junit.platform.engine.support.hierarchical.SingleTestExecutor;

public class JqwikExecutor {

	private final LifecycleRegistry registry;
	private final ExampleExecutor exampleExecutor = new ExampleExecutor();

	public JqwikExecutor(LifecycleRegistry registry) {
		this.registry = registry;
	}

	public void execute(ExecutionRequest request, TestDescriptor descriptor) {
		if (descriptor.getClass().equals(JqwikEngineDescriptor.class))
			executeContainer(request, descriptor);
		if (descriptor.getClass().equals(ContainerClassDescriptor.class))
			executeContainer(request, descriptor);
		if (descriptor.getClass().equals(ExampleMethodDescriptor.class))
			executeExample(request, (ExampleMethodDescriptor) descriptor);
		if (descriptor.getClass().equals(OverloadedExampleMethodDescriptor.class))
			executeOverloadedExample(request, (OverloadedExampleMethodDescriptor) descriptor);
	}

	private void executeOverloadedExample(ExecutionRequest request, OverloadedExampleMethodDescriptor exampleMethodDescriptor) {
		String reason = String.format("%s is overloaded", exampleMethodDescriptor.getExampleMethod().getName());
		request.getEngineExecutionListener().executionSkipped(exampleMethodDescriptor, reason);
	}

	private void executeExample(ExecutionRequest request, ExampleMethodDescriptor exampleMethodDescriptor) {
		ExampleLifecycle lifecycle = registry.lifecycleFor(exampleMethodDescriptor);
		exampleExecutor.execute(exampleMethodDescriptor, request.getEngineExecutionListener(), lifecycle);
	}

	private void executeContainer(ExecutionRequest request, TestDescriptor containerDescriptor) {
		request.getEngineExecutionListener().executionStarted(containerDescriptor);
		TestExecutionResult result = new SingleTestExecutor().executeSafely(() -> {
			for (TestDescriptor descriptor : containerDescriptor.getChildren()) {
				execute(request, descriptor);
			}
		});
		request.getEngineExecutionListener().executionFinished(containerDescriptor, result);
	}

}
