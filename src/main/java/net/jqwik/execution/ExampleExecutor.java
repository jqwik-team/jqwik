package net.jqwik.execution;

import net.jqwik.api.ExampleLifecycle;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.hierarchical.SingleTestExecutor;

import net.jqwik.JqwikException;
import net.jqwik.descriptor.ExampleMethodDescriptor;

public class ExampleExecutor {

	public void execute(ExampleMethodDescriptor methodTestDescriptor, EngineExecutionListener listener, ExampleLifecycle lifecycle) {
		listener.executionStarted(methodTestDescriptor);
		TestExecutionResult executionResult = executeExample(methodTestDescriptor, lifecycle);
		listener.executionFinished(methodTestDescriptor, executionResult);
	}

	private TestExecutionResult executeExample(ExampleMethodDescriptor methodTestDescriptor, ExampleLifecycle lifecycle) {
		Object testInstance = null;
		try {
			testInstance = ReflectionUtils.newInstance(methodTestDescriptor.gerContainerClass());
		} catch (Throwable throwable) {
			String message = String.format("Cannot create instance of class '%s'. Maybe it has no default constructor?",
					methodTestDescriptor.gerContainerClass());
			return TestExecutionResult.failed(new JqwikException(message, throwable));
		}
		return invokeExampleMethod(methodTestDescriptor, testInstance, lifecycle);
	}

	private TestExecutionResult invokeExampleMethod(ExampleMethodDescriptor exampleMethodDescriptor, Object testInstance, ExampleLifecycle lifecycle) {
		TestExecutionResult testExecutionResult = TestExecutionResult.successful();
		try {
			testExecutionResult = executeSafely(() -> ReflectionUtils.invokeMethod(exampleMethodDescriptor.getExampleMethod(), testInstance));
		} finally {
			try {
				lifecycle.doFinally(exampleMethodDescriptor, testInstance);
			} catch (Throwable ex) {
				if (testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL)
					testExecutionResult = TestExecutionResult.failed(ex);
			}
		}
		return testExecutionResult;
	}


	private TestExecutionResult executeSafely(SingleTestExecutor.Executable executable) {
		return new SingleTestExecutor().executeSafely(executable);
	}

}
