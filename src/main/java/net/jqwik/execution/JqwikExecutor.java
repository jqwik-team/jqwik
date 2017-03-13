package net.jqwik.execution;

import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import net.jqwik.discovery.ContainerClassDescriptor;
import net.jqwik.discovery.ExampleMethodDescriptor;
import org.junit.platform.engine.support.hierarchical.SingleTestExecutor;

public class JqwikExecutor {

	private ExampleExecutor exampleExecutor = new ExampleExecutor();

	public void execute(ExecutionRequest request, TestDescriptor descriptor) {
		if (descriptor instanceof EngineDescriptor)
			executeContainer(request, descriptor);
		if (descriptor instanceof ContainerClassDescriptor)
			executeContainer(request, descriptor);
		if (descriptor instanceof ExampleMethodDescriptor)
			executeExample(request, (ExampleMethodDescriptor) descriptor);
	}

	private void executeExample(ExecutionRequest request, ExampleMethodDescriptor methodTestDescriptor) {
		exampleExecutor.execute(request.getEngineExecutionListener(), methodTestDescriptor);
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
