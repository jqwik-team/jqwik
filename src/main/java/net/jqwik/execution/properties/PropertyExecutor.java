package net.jqwik.execution.properties;

import net.jqwik.JqwikException;
import net.jqwik.api.properties.ForAll;
import net.jqwik.api.properties.PropertyLifecycle;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import net.jqwik.execution.AbstractMethodExecutor;
import net.jqwik.support.JqwikReflectionSupport;
import org.junit.platform.engine.TestExecutionResult;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.platform.commons.util.BlacklistedExceptions.rethrowIfBlacklisted;
import static org.junit.platform.engine.TestExecutionResult.*;

public class PropertyExecutor extends AbstractMethodExecutor<PropertyMethodDescriptor, PropertyLifecycle> {

	private static List<Class<?>> COMPATIBLE_RETURN_TYPES = Arrays.asList(boolean.class, Boolean.class);

	private CheckedPropertyFactory checkedPropertyFactory = new CheckedPropertyFactory();

	@Override
	protected TestExecutionResult execute(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		try {
			if (hasIncompatibleReturnType(propertyMethodDescriptor.getTargetMethod())) {
				String errorMessage = String.format("Property method [%s] must return boolean value",
						propertyMethodDescriptor.getTargetMethod());
				return aborted(new JqwikException(errorMessage));
			}
			if (hasForAllParameters(propertyMethodDescriptor.getTargetMethod())) {
				return executeProperty(propertyMethodDescriptor, testInstance);
			} else {
				return executePropertyWithoutForAllParameters(propertyMethodDescriptor, testInstance);
			}
		} catch (TestAbortedException e) {
			return aborted(e);
		} catch (Throwable t) {
			rethrowIfBlacklisted(t);
			return failed(t);
		}
	}

	private boolean hasIncompatibleReturnType(Method targetMethod) {
		return !COMPATIBLE_RETURN_TYPES.contains(targetMethod.getReturnType());
	}

	private TestExecutionResult executeProperty(PropertyMethodDescriptor propertyMethodDescriptor, Object testInstance) {
		CheckedProperty property = checkedPropertyFactory.fromDescriptor(propertyMethodDescriptor, testInstance);
		return property.check();
	}

	private TestExecutionResult executePropertyWithoutForAllParameters(PropertyMethodDescriptor methodDescriptor, Object testInstance) {
		boolean success = (boolean) JqwikReflectionSupport.invokeMethod(methodDescriptor.getTargetMethod(), testInstance);

		if (success)
			return successful();
		else {
			String propertyFailedMessage = String.format("Property [%s] failed", methodDescriptor.getLabel());
			return failed(new AssertionFailedError(propertyFailedMessage));
		}
	}

	private boolean hasForAllParameters(Method targetMethod) {
		return Arrays.stream(targetMethod.getParameters()).anyMatch(parameter -> parameter.isAnnotationPresent(ForAll.class));
	}

}
