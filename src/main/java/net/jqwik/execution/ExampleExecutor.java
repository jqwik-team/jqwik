package net.jqwik.execution;

import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.hierarchical.SingleTestExecutor;

import net.jqwik.JqwikException;
import net.jqwik.discovery.ExampleMethodDescriptor;

public class ExampleExecutor {


	public void execute(EngineExecutionListener listener, ExampleMethodDescriptor methodTestDescriptor) {
		listener.executionStarted(methodTestDescriptor);
		TestExecutionResult executionResult = executeExample(methodTestDescriptor);
		listener.executionFinished(methodTestDescriptor, executionResult);
	}

	private TestExecutionResult executeExample(ExampleMethodDescriptor methodTestDescriptor) {
		Object testInstance = null;
		try {
			testInstance = ReflectionUtils.newInstance(methodTestDescriptor.gerContainerClass());
		} catch (Throwable throwable) {
			String message = String.format("Cannot create instance of class '%s'. Maybe it has no default constructor?",
					methodTestDescriptor.gerContainerClass());
			return TestExecutionResult.failed(new JqwikException(message, throwable));
		}
		return invokeExampleMethod(methodTestDescriptor, testInstance);
	}

	private TestExecutionResult invokeExampleMethod(ExampleMethodDescriptor methodTestDescriptor, Object testInstance) {
		TestExecutionResult testExecutionResult = TestExecutionResult.successful();
		try {
			testExecutionResult = executeSafely(() -> ReflectionUtils.invokeMethod(methodTestDescriptor.getExampleMethod(), testInstance));
		} finally {
			if (testInstance instanceof AutoCloseable) {
				try {
					AutoCloseable closeable = (AutoCloseable) testInstance;
					closeable.close();
				} catch(Throwable ex) {
					if (testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL)
						testExecutionResult = TestExecutionResult.failed(ex);
				}
			}
		}
		return testExecutionResult;
	}

	private TestExecutionResult executeSafely(SingleTestExecutor.Executable executable) {
		return new SingleTestExecutor().executeSafely(executable);
	}

}
