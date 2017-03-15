package net.jqwik.execution;

import net.jqwik.api.PropertyLifecycle;
import net.jqwik.descriptor.AbstractMethodDescriptor;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestExecutionResult;

import net.jqwik.JqwikException;
import net.jqwik.support.JqwikReflectionSupport;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

import java.lang.reflect.Method;

import static org.junit.platform.commons.util.BlacklistedExceptions.rethrowIfBlacklisted;
import static org.junit.platform.engine.TestExecutionResult.*;

// TODO: Merge with ExampleExecutor
public class PropertyExecutor {

	public void execute(PropertyMethodDescriptor propertyMethodDescriptor, EngineExecutionListener listener, PropertyLifecycle lifecycle) {
		if (propertyMethodDescriptor.getTargetMethod().getParameterTypes().length > 0) {
			listener.executionSkipped(propertyMethodDescriptor, "Cannot run properties with parameters - yet.");
			return;
		}
		listener.executionStarted(propertyMethodDescriptor);
		TestExecutionResult executionResult = executeProperty(propertyMethodDescriptor, lifecycle);
		listener.executionFinished(propertyMethodDescriptor, executionResult);
	}

	private TestExecutionResult executeProperty(PropertyMethodDescriptor propertyMethodDescriptor, PropertyLifecycle lifecycle) {
		Object testInstance = null;
		try {
			testInstance = JqwikReflectionSupport.newInstance(propertyMethodDescriptor.gerContainerClass());
		} catch (Throwable throwable) {
			String message = String.format("Cannot create instance of class '%s'. Maybe it has no default constructor?",
					propertyMethodDescriptor.gerContainerClass());
			return TestExecutionResult.failed(new JqwikException(message, throwable));
		}
		return invokePropertyMethod(propertyMethodDescriptor, testInstance, lifecycle);
	}

	private TestExecutionResult invokePropertyMethod(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance, PropertyLifecycle lifecycle) {
		TestExecutionResult testExecutionResult = TestExecutionResult.successful();
		try {
			testExecutionResult = execute(propertyMethodDescriptor, testInstance);
		} finally {
			try {
				lifecycle.doFinally(propertyMethodDescriptor, testInstance);
			} catch (Throwable ex) {
				if (testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL)
					testExecutionResult = TestExecutionResult.failed(ex);
			}
		}
		return testExecutionResult;
	}

	private TestExecutionResult execute(AbstractMethodDescriptor methodDescriptor, Object testInstance) {
		try {
			Object result = JqwikReflectionSupport.invokeMethod(methodDescriptor.getTargetMethod(), testInstance);
			if (!result.getClass().equals(Boolean.class))
				throw new JqwikException(String.format("Property method [%s] must return boolean value", methodDescriptor.getTargetMethod()));
			boolean success = (boolean) result;
			if (success)
				return successful();
			else {
				String propertyFailedMessage = String.format("Property [%s] failed.", methodDescriptor.getLabel());
				return failed(new AssertionFailedError(propertyFailedMessage));
			}
		}
		catch (TestAbortedException e) {
			return aborted(e);
		}
		catch (Throwable t) {
			rethrowIfBlacklisted(t);
			return failed(t);
		}
	}

}
