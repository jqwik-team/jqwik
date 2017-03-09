package net.jqwik.execution;

import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import net.jqwik.discovery.JqwikClassTestDescriptor;
import net.jqwik.discovery.JqwikExampleTestDescriptor;

public class JqwikExecutor {

	public void execute(ExecutionRequest request, TestDescriptor descriptor) {
		if (descriptor instanceof EngineDescriptor)
			executeContainer(request, descriptor);
		if (descriptor instanceof JqwikClassTestDescriptor)
			executeContainer(request, descriptor);
		if (descriptor instanceof JqwikExampleTestDescriptor)
			executeExample(request, (JqwikExampleTestDescriptor) descriptor);
	}

	private void executeExample(ExecutionRequest request, JqwikExampleTestDescriptor methodTestDescriptor) {
		request.getEngineExecutionListener().executionStarted(methodTestDescriptor);
		TestExecutionResult executionResult = executeTestMethod(methodTestDescriptor);
		request.getEngineExecutionListener().executionFinished(methodTestDescriptor, executionResult);
	}

	private TestExecutionResult executeTestMethod(JqwikExampleTestDescriptor methodTestDescriptor) {
		Object testInstance;
		try {
			testInstance = ReflectionUtils.newInstance(methodTestDescriptor.gerContainerClass());
		} catch (Throwable throwable) {
			String message = String.format("Cannot create instance of class '%s'. Maybe it has no default constructor?",
					methodTestDescriptor.gerContainerClass());
			return TestExecutionResult.failed(new RuntimeException(message, throwable));
		}
		return invokeTestMethod(methodTestDescriptor, testInstance);
	}

	private TestExecutionResult invokeTestMethod(JqwikExampleTestDescriptor methodTestDescriptor, Object testInstance) {
		try {
			ReflectionUtils.invokeMethod(methodTestDescriptor.getExampleMethod(), testInstance);
			return TestExecutionResult.successful();
		} catch (Throwable throwable) {
			return TestExecutionResult.failed(throwable);
		}
	}

	private void executeContainer(ExecutionRequest request, TestDescriptor containerDescriptor) {
		request.getEngineExecutionListener().executionStarted(containerDescriptor);
		try {
			for (TestDescriptor descriptor : containerDescriptor.getChildren()) {
				execute(request, descriptor);
			}
			request.getEngineExecutionListener().executionFinished(containerDescriptor, TestExecutionResult.successful());
		} catch (Throwable throwable) {
			request.getEngineExecutionListener().executionFinished(containerDescriptor, TestExecutionResult.failed(throwable));
		}
	}
}
