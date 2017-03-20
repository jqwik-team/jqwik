package net.jqwik.execution;

import net.jqwik.JqwikException;
import net.jqwik.api.TestLifecycle;
import net.jqwik.api.properties.ForAll;
import net.jqwik.descriptor.AbstractMethodDescriptor;
import net.jqwik.support.JqwikReflectionSupport;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestExecutionResult;

import java.util.Arrays;

abstract public class AbstractMethodExecutor {

	public void execute(AbstractMethodDescriptor methodDescriptor, EngineExecutionListener listener, TestLifecycle lifecycle) {
		if (hasUnspecifiedParameters(methodDescriptor)) {
			listener.executionSkipped(methodDescriptor, "Cannot run methods with unbound parameters - yet.");
			return;
		}
		listener.executionStarted(methodDescriptor);
		TestExecutionResult executionResult = executeExample(methodDescriptor, lifecycle);
		listener.executionFinished(methodDescriptor, executionResult);
	}

	private boolean hasUnspecifiedParameters(AbstractMethodDescriptor methodDescriptor) {
		return Arrays.stream(methodDescriptor.getTargetMethod().getParameters())
				.anyMatch(parameter -> !parameter.isAnnotationPresent(ForAll.class));
	}

	private TestExecutionResult executeExample(AbstractMethodDescriptor methodDescriptor, TestLifecycle lifecycle) {
		Object testInstance = null;
		try {
			testInstance = createTestInstance(methodDescriptor);
		} catch (Throwable throwable) {
			String message = String.format("Cannot create instance of class '%s'. Maybe it has no default constructor?",
					methodDescriptor.getContainerClass());
			return TestExecutionResult.failed(new JqwikException(message, throwable));
		}
		return invokeExampleMethod(methodDescriptor, testInstance, lifecycle);
	}

	private Object createTestInstance(AbstractMethodDescriptor methodDescriptor) {
		return JqwikReflectionSupport.newInstanceWithDefaultConstructor(methodDescriptor.getContainerClass());
	}

	private TestExecutionResult invokeExampleMethod(AbstractMethodDescriptor methodDescriptor, Object testInstance,
			TestLifecycle lifecycle) {
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
