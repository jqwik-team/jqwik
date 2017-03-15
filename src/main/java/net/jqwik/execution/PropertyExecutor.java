package net.jqwik.execution;

import static org.junit.platform.commons.util.BlacklistedExceptions.rethrowIfBlacklisted;
import static org.junit.platform.engine.TestExecutionResult.*;

import org.junit.platform.engine.TestExecutionResult;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

import net.jqwik.JqwikException;
import net.jqwik.descriptor.AbstractMethodDescriptor;
import net.jqwik.support.JqwikReflectionSupport;

public class PropertyExecutor extends AbstractMethodExecutor {

	@Override
	protected TestExecutionResult execute(AbstractMethodDescriptor methodDescriptor, Object testInstance) {
		try {
			Object result = JqwikReflectionSupport.invokeMethod(methodDescriptor.getTargetMethod(), testInstance);
			if (!result.getClass().equals(Boolean.class))
				throw new JqwikException(
						String.format("Property method [%s] must return boolean value", methodDescriptor.getTargetMethod()));
			boolean success = (boolean) result;
			if (success)
				return successful();
			else {
				String propertyFailedMessage = String.format("Property [%s] failed", methodDescriptor.getLabel());
				return failed(new AssertionFailedError(propertyFailedMessage));
			}
		} catch (TestAbortedException e) {
			return aborted(e);
		} catch (Throwable t) {
			rethrowIfBlacklisted(t);
			return failed(t);
		}
	}

}
