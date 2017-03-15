package net.jqwik.execution;

import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestExecutionResult;

import net.jqwik.JqwikException;
import net.jqwik.api.TestLifecycle;
import net.jqwik.descriptor.AbstractMethodDescriptor;
import net.jqwik.support.JqwikReflectionSupport;

abstract class AbstractMethodExecutor {

	public void execute(AbstractMethodDescriptor methodDescriptor, EngineExecutionListener listener, TestLifecycle lifecycle) {
		if (methodDescriptor.getTargetMethod().getParameterTypes().length > 0) {
			listener.executionSkipped(methodDescriptor, "Cannot run examples with parameters - yet.");
			return;
		}
		listener.executionStarted(methodDescriptor);
		TestExecutionResult executionResult = executeExample(methodDescriptor, lifecycle);
		listener.executionFinished(methodDescriptor, executionResult);
	}

	private TestExecutionResult executeExample(AbstractMethodDescriptor methodDescriptor, TestLifecycle lifecycle) {
		Object testInstance = null;
		try {
			testInstance = JqwikReflectionSupport.newInstance(methodDescriptor.gerContainerClass());
		} catch (Throwable throwable) {
			String message = String.format("Cannot create instance of class '%s'. Maybe it has no default constructor?",
					methodDescriptor.gerContainerClass());
			return TestExecutionResult.failed(new JqwikException(message, throwable));
		}
		return invokeExampleMethod(methodDescriptor, testInstance, lifecycle);
	}

	private TestExecutionResult invokeExampleMethod(AbstractMethodDescriptor methodDescriptor, Object testInstance, TestLifecycle lifecycle) {
		TestExecutionResult testExecutionResult = TestExecutionResult.successful();
		try {
			testExecutionResult = execute(methodDescriptor, testInstance);
		} finally {
			try {
				lifecycle.doFinally(methodDescriptor, testInstance);
			} catch (Throwable ex) {
				if (testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL)
					testExecutionResult = TestExecutionResult.failed(ex);
			}
		}
		return testExecutionResult;
	}

	protected abstract TestExecutionResult execute(AbstractMethodDescriptor methodDescriptor, Object testInstance);

}
